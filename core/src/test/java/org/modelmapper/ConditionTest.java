import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Assert;

import java.util.List;
public class ConditionTest{


static class Obj {
    private String a;
    private String b;
    private String c;
    private String d;
    private String e;

    public Obj(String a, String b, String c, String d, String e) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
    }

    @Override
    public String toString() {
        return "a:" + a +
                " b:" + b +
                " c:" + c +
                " d:" + d +
                " e:" + e;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }
}

    public static void main(String[] args) {
        ModelMapper mapper = new ModelMapper();
        Obj obj1 = new Obj("1","2","3","4","5");
        Obj obj2 = new Obj("10","20",null,null,null);
        mapper.getConfiguration().setPropertyCondition(Conditions.isNull());
        mapper.map(obj2,obj1);
        System.out.println(obj1);
        System.out.println(obj2);

    }
}