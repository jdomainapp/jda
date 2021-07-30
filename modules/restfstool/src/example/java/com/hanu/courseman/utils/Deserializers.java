package com.hanu.courseman.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.hanu.courseman.modules.address.model.Address;
import com.hanu.courseman.modules.coursemodule.model.CourseModule;
import com.hanu.courseman.modules.enrolment.model.Enrolment;
import com.hanu.courseman.modules.student.model.Student;
import com.hanu.courseman.modules.studentclass.model.StudentClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Define all nested serializers.
 */
public class Deserializers {

    private static class CollectionTypedDeserializer<T> extends StdDeserializer<Collection<T>> {
        private final Class<T> vc;

        public CollectionTypedDeserializer(Class vc) {
            super(vc);
            this.vc = vc;
        }

        @Override
        public Collection<T> deserialize(JsonParser parser,
                                               DeserializationContext ctxt)
                throws IOException {

            Collection<T> result = new ArrayList<>();
            while (parser.nextToken() == JsonToken.START_OBJECT) {
                T item = parser.readValueAs(vc);
                result.add(item);
            }
            return result;
        }
    }

    private static class TypedDeserializer<T> extends StdDeserializer<T> {
        private final Class<T> vc;

        public TypedDeserializer(Class vc) {
            super(vc);
            this.vc = vc;
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
            return p.readValueAs(vc);
        }
    }

    public static class StudentCollectionDeserializer
            extends CollectionTypedDeserializer<Student> {
        public StudentCollectionDeserializer() {
            super(Student.class);
        }
    }

    public static class EnrolmentCollectionDeserializer
            extends CollectionTypedDeserializer<Enrolment> {
        public EnrolmentCollectionDeserializer() {
            super(Enrolment.class);
        }
    }

    public static class StudentDeserializer extends TypedDeserializer<Student> {
        public StudentDeserializer() {
            super(Student.class);
        }
    }

    public static class CourseModuleDeserializer extends TypedDeserializer<CourseModule> {
        public CourseModuleDeserializer() {
            super(CourseModule.class);
        }
    }

    public static class AddressDeserializer extends TypedDeserializer<Address> {
        public AddressDeserializer() {
            super(Address.class);
        }
    }

    public static class StudentClassDeserializer extends TypedDeserializer<StudentClass> {
        public StudentClassDeserializer() {
            super(StudentClass.class);
        }
    }
}
