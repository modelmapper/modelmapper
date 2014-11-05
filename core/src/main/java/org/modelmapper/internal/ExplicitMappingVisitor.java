package org.modelmapper.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.internal.PropertyInfoImpl.FieldPropertyInfo;
import org.modelmapper.internal.util.Members;
import org.modelmapper.internal.util.Primitives;
import org.modelmapper.spi.NameableType;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

/**
 * Visits explicitly declared mappings, capturing mapping information.
 * 
 * @author Jonathan Halterman
 */
public class ExplicitMappingVisitor extends ClassVisitor {
  private static final String SOURCE_FIELD = "source";
  private static final String DEST_FIELD = "destination";
  private static final String PROXY_FIELD_DESC = "Ljava/lang/Object;";
  private static final String CONFIGURE_METHOD = "configure";
  private static final String CONFIGURE_METHOD_DESC = "()V";
  private static final String ACCESSOR_METHOD_DESC_PREFIX = "()";

  private static final String MAP_METHOD = "map";
  private static final String SKIP_METHOD = "skip";
  private static final String MAP_EXPR_OWNER_PREFIX = "org/modelmapper/builder";
  private static final String MAP_DEST_METHOD_DESC = "()Ljava/lang/Object;";
  private static final String MAP_SOURCE_METHOD_DESC = "(Ljava/lang/Object;)Ljava/lang/Object;";
  private static final String SKIP_DEST_METHOD_DESC = "(Ljava/lang/Object;)V";
  private static final String MAP_BOTH_METHOD_DESC = "(Ljava/lang/Object;Ljava/lang/Object;)V";

  private final Errors errors;
  private final InheritingConfiguration config;
  private final String propMapClassInternalName;
  private final String destClassInternalName;
  private final ClassLoader propertyMapClassLoader;
  private final Set<String> syntheticFields = new HashSet<String>();

  /** Result mappings */
  final List<VisitedMapping> mappings = new ArrayList<VisitedMapping>();

  static class VisitedMapping {
    List<Accessor> sourceAccessors = new ArrayList<Accessor>();
    List<Accessor> destinationAccessors = new ArrayList<Accessor>();
    List<Mutator> destinationMutators = new ArrayList<Mutator>();
  }

  public ExplicitMappingVisitor(Errors errors, InheritingConfiguration config,
      String propertyMapClassName, String destinationClassName, ClassLoader propertyMapClassLoader) {
    super(Opcodes.ASM5);
    this.errors = errors;
    this.config = config;
    propMapClassInternalName = propertyMapClassName.replace('.', '/');
    destClassInternalName = destinationClassName.replace('.', '/');
    this.propertyMapClassLoader = propertyMapClassLoader;
  }

  @Override
  public FieldVisitor visitField(int access, String name, String desc, String signature,
      Object value) {
    if ((access & Opcodes.ACC_SYNTHETIC) == Opcodes.ACC_SYNTHETIC)
      syntheticFields.add(name);
    return null;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature,
      String[] exceptions) {
    if (name.equals(CONFIGURE_METHOD) && desc.equals(CONFIGURE_METHOD_DESC))
      return new MappingCapturingVisitor();
    return null;
  }

  private class MappingCapturingVisitor extends MethodVisitor {
    private final List<AbstractInsnNode> instructions = new ArrayList<AbstractInsnNode>();

    /** Per mapping state */
    private VisitedMapping mapping = new VisitedMapping();
    /** The class that owns the last expected mutator method */
    private String lastMutatorOwner;
    /** 0=none, 1=source, 2=destination */
    private int subjectType;
    /** 0=none, 1=map(), 2=map(Object), 3=skip(Object) */
    private int mapType;

    private MappingCapturingVisitor() {
      super(Opcodes.ASM5);
    }

    @Override
    public void visitInsn(int opcode) {
      if (opcode == Opcodes.RETURN)
        instructions.add(new InsnNode(opcode));
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
      // If ALOAD and previous instruction was a map(Object, Object) or mutator
      if (opcode == Opcodes.ALOAD) {
        if (!instructions.isEmpty()) {
          AbstractInsnNode previous = instructions.get(instructions.size() - 1);
          if (isMethodInvocation(previous)) {
            MethodInsnNode mn = (MethodInsnNode) previous;
            if (isMapBothMethod(mn) || !isAccessor(mn))
              instructions.add(new InsnNode(opcode));
          }
        }
      }
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name,
        final String desc) {
      // If GETFIELD and not owned by property map or not synthetic
      if (opcode == Opcodes.GETFIELD
          && (!owner.equals(propMapClassInternalName) || !syntheticFields.contains(name)))
        instructions.add(new FieldInsnNode(opcode, owner, name, desc));
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
      boolean isSourceMethod = owner.equals(propMapClassInternalName)
          && (name.equals(SOURCE_FIELD) || name.equals(DEST_FIELD));

      // If not special and not source method invocation and owner is not a primitive wrapper
      if (opcode != Opcodes.INVOKESPECIAL && !isSourceMethod
          && !Primitives.isPrimitiveWrapperInternalName(owner))
        instructions.add(new MethodInsnNode(opcode, owner, name, desc, itf));
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
      instructions.add(new InvokeDynamicInsnNode(name, desc, bsm, bsmArgs));
    }

    @Override
    public void visitEnd() {
      for (int i = 0; i < instructions.size(); i++) {
        AbstractInsnNode ins = instructions.get(i);

        if (ins.getOpcode() == Opcodes.GETFIELD) {
          FieldInsnNode fn = (FieldInsnNode) ins;

          if (fn.owner.equals(propMapClassInternalName) && fn.desc.equals(PROXY_FIELD_DESC)) {
            if (fn.name.equals(SOURCE_FIELD))
              subjectType = 1;
            else if (fn.name.equals(DEST_FIELD))
              subjectType = 2;
            continue;
          }

          Class<?> ownerType = classFor(fn.owner.replace('/', '.'));
          if (subjectType == 1)
            recordSourceField(ownerType, fieldFor(ownerType, fn));
          else if (subjectType == 2) {
            setLastMutatorOwner(Type.getType(fn.desc));
            recordDestinationField(ownerType, fieldFor(ownerType, fn));
          }
        } else if (isMethodInvocation(ins)) {
          MethodInsnNode mn = (MethodInsnNode) ins;

          if (isMapMethod(mn) || isSkipMethod(mn)) {
            lastMutatorOwner = destClassInternalName;
            if (MAP_DEST_METHOD_DESC.equals(mn.desc)) {
              mapType = 1;
              subjectType = 2;
            } else if (MAP_SOURCE_METHOD_DESC.equals(mn.desc)) {
              // If already recorded destination field
              if (subjectType == 2) {
                recordProperties();
              } else {
                mapType = 2;
                subjectType = 2;
              }
            } else if (MAP_BOTH_METHOD_DESC.equals(mn.desc)) {
              recordProperties();
            } else if (SKIP_DEST_METHOD_DESC.equals(mn.desc)) {
              mapType = 3;
            }
          } else if (subjectType != 0) {
            Class<?> ownerType = classFor(mn.owner.replace('/', '.'));
            Type methodType = Type.getMethodType(mn.desc);

            // If last destination mutator
            if (mapType != 0 && !isAccessor(mn) && mn.owner.equals(lastMutatorOwner)) {
              recordDestinationMethod(ownerType, methodFor(ownerType, methodType, mn));
              recordProperties();
            } else {
              if (subjectType == 1)
                recordSourceMethod(ownerType, methodFor(ownerType, methodType, mn));
              else if (subjectType == 2) {
                setLastMutatorOwner(methodType.getReturnType());
                recordDestinationMethod(ownerType, methodFor(ownerType, methodType, mn));
              }
            }
          }
        } else if (ins.getOpcode() == Opcodes.ALOAD || ins.getOpcode() == Opcodes.RETURN) {
          // If skip(Object)
          if (mapType == 3 && subjectType != 0)
            recordProperties();
          else if (mapType != 0)
            errors.missingDestination();
        }
      }
    }

    private void setLastMutatorOwner(Type type) {
      int sort = type.getSort();
      if (sort == Type.ARRAY || sort == Type.OBJECT)
        lastMutatorOwner = type.getInternalName();
    }

    private boolean isMethodInvocation(AbstractInsnNode ins) {
      return ins.getOpcode() == Opcodes.INVOKEVIRTUAL || ins.getOpcode() == Opcodes.INVOKEINTERFACE;
    }

    private boolean isMapMethod(MethodInsnNode mn) {
      return mn.name.equals(MAP_METHOD)
          && (mn.owner.equals(propMapClassInternalName) || mn.owner.startsWith(MAP_EXPR_OWNER_PREFIX));
    }

    private boolean isSkipMethod(MethodInsnNode mn) {
      return mn.name.equals(SKIP_METHOD)
          && (mn.owner.equals(propMapClassInternalName) || mn.owner.startsWith(MAP_EXPR_OWNER_PREFIX));
    }

    private boolean isMapBothMethod(MethodInsnNode mn) {
      return mn.name.equals(MAP_METHOD)
          && (mn.owner.equals(propMapClassInternalName) || mn.owner.startsWith(MAP_EXPR_OWNER_PREFIX))
          && mn.desc.equals(MAP_BOTH_METHOD_DESC);
    }

    private boolean isAccessor(MethodInsnNode mn) {
      return mn.desc.startsWith(ACCESSOR_METHOD_DESC_PREFIX);
    }

    private void recordProperties() {
      mappings.add(mapping);
      mapping = new VisitedMapping();
      lastMutatorOwner = null;
      subjectType = 0;
      mapType = 0;
    }

    private void recordSourceMethod(Class<?> type, Method method) {
      assertNotFinal(method);
      if (PropertyInfoResolver.ACCESSORS.isValid(method)) {
        String propertyName = config.getSourceNameTransformer().transform(method.getName(),
            NameableType.METHOD);
        mapping.sourceAccessors.add(PropertyInfoRegistry.accessorFor(type, method, config,
            propertyName));
      } else
        errors.invalidSourceMethod(method);
    }

    private void recordSourceField(Class<?> type, Field field) {
      assertNotFinal(field);
      if (PropertyInfoResolver.FIELDS.isValid(field)) {
        String propertyName = config.getSourceNameTransformer().transform(field.getName(),
            NameableType.FIELD);
        mapping.sourceAccessors.add(PropertyInfoRegistry.fieldPropertyFor(type, field, config,
            propertyName));
      } else
        errors.invalidSourceField(field);
    }

    private void recordDestinationMethod(Class<?> type, Method method) {
      assertNotFinal(method);
      if (PropertyInfoResolver.MUTATORS.isValid(method)) {
        String propertyName = config.getDestinationNameTransformer().transform(method.getName(),
            NameableType.METHOD);
        mapping.destinationMutators.add(PropertyInfoRegistry.mutatorFor(type, method, config,
            propertyName));
      } else if (PropertyInfoResolver.ACCESSORS.isValid(method)) {
        String propertyName = config.getSourceNameTransformer().transform(method.getName(),
            NameableType.METHOD);
        mapping.destinationAccessors.add(PropertyInfoRegistry.accessorFor(type, method, config,
            propertyName));

        // Find mutator corresponding to accessor
        Mutator mutator = TypeInfoRegistry.typeInfoFor(type, config).mutatorForAccessorMethod(
            method.getName());
        if (mutator != null)
          mapping.destinationMutators.add(mutator);
        else
          errors.missingMutatorForAccessor(method);
      } else
        errors.invalidDestinationMethod(method);
    }

    private void recordDestinationField(Class<?> type, Field field) {
      assertNotFinal(field);
      if (PropertyInfoResolver.FIELDS.isValid(field)) {
        String propertyName = config.getDestinationNameTransformer().transform(field.getName(),
            NameableType.FIELD);
        FieldPropertyInfo propertyInfo = PropertyInfoRegistry.fieldPropertyFor(type, field, config,
            propertyName);
        mapping.destinationAccessors.add(propertyInfo);
        mapping.destinationMutators.add(propertyInfo);
      } else
        errors.invalidDestinationField(field);
    }

    private void assertNotFinal(Member member) {
      if (Modifier.isFinal(member.getDeclaringClass().getModifiers()))
        errors.invocationAgainstFinalClass(member.getDeclaringClass());
      if (member instanceof Method && Modifier.isFinal(member.getModifiers()))
        errors.invocationAgainstFinalMethod(member);
    }
  }

  private Field fieldFor(Class<?> type, FieldInsnNode fn) {
    return Members.fieldFor(type, fn.name);
  }

  private Method methodFor(Class<?> type, Type methodType, MethodInsnNode mn) {
    Type[] argumentTypes = methodType.getArgumentTypes();
    Class<?>[] paramTypes = new Class<?>[argumentTypes.length];
    for (int i = 0; i < paramTypes.length; i++) {
      Class<?> paramType = Primitives.primitiveFor(argumentTypes[i]);
      paramTypes[i] = paramType == null ? classFor(argumentTypes[i].getClassName()) : paramType;
    }
    return Members.methodFor(type, mn.name, paramTypes);
  }

  private Class<?> classFor(String className) {
    try {
      return Class.forName(className, true, propertyMapClassLoader);
    } catch (ClassNotFoundException e) {
      throw errors.errorResolvingClass(e, className).toException();
    }
  }
}
