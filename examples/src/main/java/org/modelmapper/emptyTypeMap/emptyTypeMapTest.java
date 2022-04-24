package org.modelmapper.emptyTypeMap;

import org.modelmapper.ModelMapper;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * From https://github.com/modelmapper/modelmapper/issues/572#issue-755595999
 */

public class emptyTypeMapTest {
    public static class Animal{
        String name;
        int weight;
        public String getName() {
            return name;
        }

        public void SetName(String name) {
            this.name = name;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

    }

    public static class AnimalDTO {
        String name;
        int weight;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }


    }

    public static void main(String[] args) {
        ModelMapper modelMapper = new ModelMapper();
        Animal dog = new Animal();
        dog.name = "dog";
        dog.weight = 50;

        modelMapper.emptyTypeMap(Animal.class, AnimalDTO.class, "turning").addMappings(mapper -> {mapper.map(Animal::getName, AnimalDTO::setName); mapper.map(Animal::getWeight, AnimalDTO::setWeight);});
        AnimalDTO dog_DTO = modelMapper.map(dog, AnimalDTO.class, "turning");

        assertEquals(dog.weight, dog_DTO.weight);
        assertEquals(dog.name, dog_DTO.name);

        modelMapper.emptyTypeMap(Animal.class, AnimalDTO.class, "fail");
        AnimalDTO dog_DTO2 = modelMapper.map(dog, AnimalDTO.class, "fail");

        assertNotEquals(dog.weight, dog_DTO2.weight);
        assertNotEquals(dog.name, dog_DTO2.name);

    }
}
