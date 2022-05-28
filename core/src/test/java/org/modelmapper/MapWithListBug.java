package org.modelmapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.convention.MatchingStrategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapWithListBug {

    private ModelMapper modelMapper;

    @Before
    public void init() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        //http://modelmapper.org/getting-started/#validating-matches
        modelMapper.validate();
    }


    @Test
    public void testMapToDto() {
        A source = new A();
        source.setAuthor("Author");
        Map<String, List<B>> map = new HashMap<>();
        List<B> list = new ArrayList<>();
        B b = new B();
        b.setTitle("a title");
        list.add(b);
        map.put("key 1", list);
        source.setMyMap(map);

        ADto aDto = modelMapper.map(source, ADto.class);

        Assert.assertEquals(1, aDto.getMyMap().size());
        Assert.assertEquals(aDto.getMyMap().get("key 1").get(0).getClass(), BDto.class);
    }

}

class A {

    private String author;
    private Map<String, List<B>> myMap;

    public A() {
        this.myMap = new HashMap<>();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Map<String, List<B>> getMyMap() {
        return myMap;
    }

    public void setMyMap(Map<String, List<B>> myMap) {
        this.myMap = myMap;
    }
}


class B {

    private String title;

    public B() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}


class ADto {

    private String author;
    private Map<String, List<BDto>> myMap;

    public ADto() {
        this.myMap = new HashMap<>();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Map<String, List<BDto>> getMyMap() {
        return myMap;
    }

    public void setMyMap(Map<String, List<BDto>> myMap) {
        this.myMap = myMap;
    }
}


class BDto {

    private String title1;

    public BDto() {
    }

    public String getTitle() {
        return title1;
    }

    public void setTitle(String title) {
        this.title1 = title;
    }

}

