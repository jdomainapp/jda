package org.jda.example.courseman.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.json.stream.JsonParserFactory;

import org.apache.kafka.common.serialization.Serializer;
import org.jda.example.courseman.modules.address.model.Address;
import org.jda.example.courseman.modules.coursemodule.model.CourseModule;
import org.jda.example.courseman.modules.enrolment.model.Enrolment;
import org.jda.example.courseman.modules.student.model.Student;
import org.jda.example.courseman.modules.studentclass.model.StudentClass;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import jda.modules.common.io.ToolkitIO;

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
    } /**END: {@link TypedDeserializer}*/ 

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
