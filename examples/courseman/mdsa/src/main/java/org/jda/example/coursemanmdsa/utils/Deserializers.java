package org.jda.example.coursemanmdsa.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.jda.example.coursemanmdsa.modules.address.model.Address;
import org.jda.example.coursemanmdsa.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanmdsa.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmdsa.modules.student.model.Student;
import org.jda.example.coursemanmdsa.modules.studentclass.model.StudentClass;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

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

    public static class StudCollectionDeserializer
            extends CollectionTypedDeserializer<Student> {
        public StudCollectionDeserializer() {
            super(Student.class);
        }
    }

    public static class EnrCollectionDeserializer
            extends CollectionTypedDeserializer<Enrolment> {
        public EnrCollectionDeserializer() {
            super(Enrolment.class);
        }
    }

    public static class StudDeserializer extends TypedDeserializer<Student> {
        public StudDeserializer() {
            super(Student.class);
        }
    }

    public static class CModDeserializer extends TypedDeserializer<CourseModule> {
        public CModDeserializer() {
            super(CourseModule.class);
        }
    }

    public static class AddrDeserializer extends TypedDeserializer<Address> {
        public AddrDeserializer() {
            super(Address.class);
        }
    }

    public static class StudClsDeserializer extends TypedDeserializer<StudentClass> {
        public StudClsDeserializer() {
            super(StudentClass.class);
        }
    }
}
