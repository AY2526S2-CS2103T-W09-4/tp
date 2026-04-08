package seedu.taskforge.logic.commands.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.taskforge.commons.core.GuiSettings;
import seedu.taskforge.logic.commands.CommandResult;
import seedu.taskforge.model.Model;
import seedu.taskforge.model.ReadOnlyAddressBook;
import seedu.taskforge.model.ReadOnlyUserPrefs;
import seedu.taskforge.model.person.Person;
import seedu.taskforge.model.project.Project;

public class ListProjectCommandTest {

    @Test
    public void execute_noProjects_showsNone() {
        ModelStubWithProjectList modelStub = new ModelStubWithProjectList();

        CommandResult result = new ListProjectCommand().execute(modelStub);

        assertEquals(ListProjectCommand.MESSAGE_SUCCESS + " None", result.getFeedbackToUser());
    }

    @Test
    public void execute_withProjects_showsAllProjects() {
        Project alpha = new Project("alpha");
        Project beta = new Project("beta");
        ModelStubWithProjectList modelStub = new ModelStubWithProjectList(alpha, beta);

        CommandResult result = new ListProjectCommand().execute(modelStub);

        String expected = ListProjectCommand.MESSAGE_SUCCESS + "\n"
                + "1. " + alpha.title + "\n"
                + "2. " + beta.title;
        assertEquals(expected, result.getFeedbackToUser());
    }

    @Test
    public void equals() {
        ListProjectCommand viewAllProjectCommand = new ListProjectCommand();

        assertTrue(viewAllProjectCommand.equals(viewAllProjectCommand));
        assertTrue(viewAllProjectCommand.equals(new ListProjectCommand()));
        assertFalse(viewAllProjectCommand.equals(1));
        assertFalse(viewAllProjectCommand.equals(null));
    }

    @Test
    public void toStringMethod() {
        String expected = ListProjectCommand.class.getCanonicalName()
                + "{subcommandWord=" + ListProjectCommand.SUBCOMMAND_WORD + "}";
        assertEquals(expected, new ListProjectCommand().toString());
    }

    @Test
    public void hashCodeMethod() {
        assertEquals(ListProjectCommand.SUBCOMMAND_WORD.hashCode(), new ListProjectCommand().hashCode());
    }

    /**
     * A default model stub that has all methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook addressBook) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasProject(Project project) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteProject(Project target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addProject(Project project) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setProject(Project target, Project editedProject) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Project> getProjectList() {
            return FXCollections.observableArrayList();
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub with a configurable project list.
     */
    private class ModelStubWithProjectList extends ModelStub {
        private final ObservableList<Project> projects;

        ModelStubWithProjectList(Project... projects) {
            this.projects = FXCollections.observableArrayList(projects);
        }

        @Override
        public ObservableList<Project> getProjectList() {
            return projects;
        }
    }
}
