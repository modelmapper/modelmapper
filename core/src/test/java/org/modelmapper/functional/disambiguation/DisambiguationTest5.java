package org.modelmapper.functional.disambiguation;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.Provider;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class DisambiguationTest5 extends AbstractTest {
  static class Source {
    Prop<?> someValue = new SubProp("Some String");
  }
  
  static abstract class Prop<ValueType> {
    public abstract ValueType getValue();
    public abstract void setValue(ValueType value);
  }
  
  static class SubProp extends Prop<String>{
	String value;
	public SubProp(final String value) {
	  this.value = value;
	}
	  
	@Override
	public String getValue() {
	  return value;
	}

	@Override
	public void setValue(final String value) {
		this.value = value;
	}
  }

  static class Dest {
    Prop2<?> someValue;
    
	public void setSomeValue(final Prop2<?> someValue) {
		this.someValue = someValue;
	}
  }
  

  static abstract class Prop2<ValueType> {
    public abstract ValueType getValue();
    public abstract void setValue(ValueType value);
  }
  
  static class SubProp2 extends Prop2<String> {
    String value;
  
    @Override
	public void setValue(final String value) {
	  this.value = value;
    }
    
	@Override
	public String getValue() {
      return value;
	}
  }
  
  static class Prop2Provider implements Provider<Object>{

	@Override
	public Object get(final org.modelmapper.Provider.ProvisionRequest<Object> request) {
		if(Prop2.class.equals(request.getRequestedType()) ){
			return new SubProp2();
		}
		return null;
	}
	  
  }

  public void shouldAllowMappingOfNestedPropertiesInComplexGenericTypes() {
    Source source = new Source();
    modelMapper.getConfiguration().setProvider(new Prop2Provider());
	Dest d = modelMapper.map(source, Dest.class);

    assertEquals(source.someValue.getValue(), d.someValue.getValue());
  }
}
