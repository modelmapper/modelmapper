package org.modelmapper.internal.util;

import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static org.testng.Assert.assertEquals;

@Test
public class IterablesTest {

    public void shouldGetFirstIndexFromSet() {
        Collection<Object> collection = new HashSet<Object>();
        String testValue = "test value";
        collection.add(testValue);

        assertEquals(Iterables.getElementFromCollection(collection, 0), testValue);
    }

    public void shouldGetLastIndexFromSet() {
        Collection<Object> collection = new LinkedHashSet<Object>();
        String testValue = "test value";
        collection.add(testValue);
        String lastTestValue = "last test value";
        collection.add(lastTestValue);

        assertEquals(Iterables.getElementFromCollection(collection, collection.size() - 1), lastTestValue);
    }
}
