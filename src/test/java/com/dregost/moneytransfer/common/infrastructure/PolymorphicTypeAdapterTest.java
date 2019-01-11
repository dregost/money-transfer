package com.dregost.moneytransfer.common.infrastructure;

import com.google.gson.*;
import lombok.*;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test
public class PolymorphicTypeAdapterTest {

    private static final SampleClass SAMPLE_OBJECT = SampleClass.of(5);
    private static final Class<SampleInterface> SAMPLE_TYPE = SampleInterface.class;

    public void serialize_shouldAddType() {
        val serializer = makeGson(SAMPLE_TYPE);

        val result = serializer.toJson(SAMPLE_OBJECT);

        assertThat(result).contains("type", SampleClass.class.getName(), "sampleField", "5");
    }

    public void deserialize_shouldDeserializeToConcreteType() {
        val serialized = String.format("{\"type\":\"%s\",\"data\":{\"sampleField\":5}}", SampleClass.class.getName());
        val deserializer = makeGson(SAMPLE_TYPE);

        val result = deserializer.fromJson(serialized, SAMPLE_TYPE);

        assertThat(result).isEqualTo(SAMPLE_OBJECT);
    }

    public <T> Gson makeGson(final Class<T> cls) {
        val polymorphicTypeAdapter = PolymorphicTypeAdapter.<T>of(new Gson());
        return new GsonBuilder().registerTypeHierarchyAdapter(cls, polymorphicTypeAdapter).create();
    }

    private interface SampleInterface {
    }

    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    private static class SampleClass implements SampleInterface {
        private int sampleField;
    }
}