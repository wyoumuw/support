package com.youmu;

import com.google.common.collect.Lists;
import com.youmu.common.wrapper.ObjectMapWrapper;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2017/12/29
 */
public class WrapperTest {

    @Test
    public void objectMapWrapper() {
        List<Person> people = Lists.newArrayList(new Person(1, "博丽", "灵梦", 16),
                new Person(2, "十六夜", "咲夜", 16), new Person(2, "八云", "紫", 17));

        Map<Integer, Integer> groupedMap = people.stream()
                .collect(Collectors.groupingBy(Person::getId)).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .map(Person::getAge).mapToInt(Integer::intValue).sum()));
        System.out.println(groupedMap);

        // mysql:select * from person group by age
        Set<Person> persons = people
                .stream().map(
                        person -> new ObjectMapWrapper.Builder<>(person)
                                .setEqualsFunction((p,
                                        o) -> p.age == ((ObjectMapWrapper<Person>) o)
                                                .getTarget().age)
                                .setHashCodeFunction(Person::getAge).build())
                .collect(Collectors.toSet()).stream().map(ObjectMapWrapper::getTarget)
                .collect(Collectors.toSet());
        System.out.println(persons);
    }

    @Test
    public void allNull() {
        List<Person> people = Lists.newArrayList();
        System.out.println(people.stream().mapToInt(Person::getAge).average().orElse(-1));
    }

    static class Person {
        private Integer id;
        private String firstName;
        private String lastName;
        private int age;

        public Person(String firstName, String lastName, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Person(Integer id, String firstName, String lastName, int age) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Person{" + "firstName=" + firstName + ", lastName='" + lastName + '\''
                    + ", age=" + age + '}';
        }
    }
}
