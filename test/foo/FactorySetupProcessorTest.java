package foo;

import akula.factory.MockPersistenceHandler;
import org.junit.Test;

import java.util.List;

import static akula.factory.Factories.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.springframework.util.Assert.isInstanceOf;

public class FactorySetupProcessorTest {

    @Test
    public void shouldInstantiateAndSetup() {
        Foo foo = newFoo().build();
        assertEquals(5, foo.fu);
        assertEquals("bar", foo.bar);
    }

    @Test
    public void shouldInstantiateOverrideSetup() {
        Foo foo = newFoo().setBar("ef").setFu(3).build();
        assertEquals(3, foo.fu);
        assertEquals("ef", foo.bar);
    }

    @Test
    public void shouldInstantiateAlias() {
        Foo fu = newFu().build();
        assertEquals(10, fu.fu);
        assertEquals("bur", fu.bar);
    }

    @Test
    public void shouldCallPersistenceCallbackAfterBuild() {
        newFoo().create();
        assertTrue(MockPersistenceHandler.assertCallbackCalledWith("mydb"));
    }

    @Test
    public void shouldNotPersistComposite() {
        newInstructor().create();
        List<MockPersistenceHandler.Log> logs = MockPersistenceHandler.logs();
        assertEquals(1, logs.size());
        isInstanceOf(Instructor.class, logs.get(0).object);
    }

    @Test
    public void shouldPersistEachAssociation() {
        newDriver().create();
        List<MockPersistenceHandler.Log> logs = MockPersistenceHandler.logs();
        assertEquals(3, logs.size());
        isInstanceOf(Tag.class, logs.get(0).object);
        isInstanceOf(License.class, logs.get(1).object);
        isInstanceOf(Driver.class, logs.get(2).object);
    }

    @Test
    public void shouldNotAssociationIfOverriddenInSetterChain() {
        newDriver().setLicenseId("456").create();
        List<MockPersistenceHandler.Log> logs = MockPersistenceHandler.logs();
        assertEquals(1, logs.size());
        isInstanceOf(Driver.class, logs.get(0).object);
    }
}
