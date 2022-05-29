

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;

import java.util.List;

public class MyObject {
    private String myStringProperty;
    private List<String> myListProperty;

    public MyObject(String myStringProperty, List<String> myListProperty) {
        this.myStringProperty = myStringProperty;
        this.myListProperty = myListProperty;
    }

    public String getMyStringProperty() {
        return myStringProperty;
    }

    public void setMyStringProperty(String myStringProperty) {
        this.myStringProperty = myStringProperty;
    }

    public List<String> getMyListProperty() {
        return myListProperty;
    }

    public void setMyListProperty(List<String> myListProperty) {
        this.myListProperty = myListProperty;
    }

    @Override
    public String toString() {
        return "MyObject{" +
                "myStringProperty='" + myStringProperty + '\'' +
                ", myListProperty=" + myListProperty +
                '}';
    }
    public static void main(String[] args) {
        test1();
        test2();
        System.out.println("------------------- END    -------------------");
    }
    private static void test1(){
        ModelMapper mapper = new ModelMapper();
        MyObject obj = new MyObject("bar", List.of("a", "b", "c"));
        MyObject updates = new MyObject( null,List.of("a", "b", "c", "d"));
        // obj should be updated using the updates object. All null values should not be updated.

        System.out.println("------------------- TEST 1 -------------------");
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        mapper.map(updates,obj);
        System.out.println(obj);
        System.out.println("SUCCEED -- Excepted:  MyObject{myStringProperty='bar', myListProperty=[a, b, c, d]}");
    }
    private static void test2(){
        ModelMapper mapper = new ModelMapper();
        var obj = new MyObject("bar", List.of("a", "b", "c"));
        var updates = new MyObject(null, List.of("a", "b", "c", "d"));

        // obj should be updated using the updates object. All null values should not be updated.

        System.out.println("------------------- TEST 2 -------------------");
        // ctx.getDestination() -> Always null, expected List.of("a", "b", "c")
        mapper.getConfiguration().setPropertyCondition(ctx -> {
            System.out.println("Destination: " + ctx.getDestination());
            return ctx.getDestination() != null;
        });
        mapper.map(updates,obj );
        System.out.println(obj);
        System.out.println("SUCCEED -- Excepted:  MyObject{myStringProperty='bar', myListProperty=[a, b, c, d]}");
    }
}
