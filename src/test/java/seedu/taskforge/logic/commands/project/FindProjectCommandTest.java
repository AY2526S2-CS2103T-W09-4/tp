package seedu.taskforge.logic.commands.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Arrays;
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
import seedu.taskforge.model.project.UniqueProjectList;

public class FindProjectCommandTest {

    @Test
    public void execute_matchingProjects_success() {
        ModelStubWithProjectList modelStub = new ModelStubWithProjectList(
                new Project("Alpha"),
                new Project("Alpha Beta"),
                new Project("Gamma")
        );

        FindProjectCommand command = new FindProjectCommand(Arrays.asList("alpha"));
        CommandResult result = command.execute(modelStub);

        assertEquals("Found projects:\n1. Alpha\n2. Alpha Beta", result.getFeedbackToUser());
    }

    @Test
    public void execute_noMatchingProjects_success() {
        ModelStubWithProjectList modelStub = new ModelStubWithProjectList(
                new Project("Alpha"),
                new Project("Beta")
        );

        FindProjectCommand command = new FindProjectCommand(Arrays.asList("gamma"));
        CommandResult result = command.execute(modelStub);

        assertEquals(FindProjectCommand.MESSAGE_NO_PROJECT_FOUND, result.getFeedbackToUser());
    }

    @Test
    public void equals() {
        FindProjectCommand findFirstCommand =
                new FindProjectCommand(Arrays.asList("alpha"));
        FindProjectCommand findSecondCommand =
                new FindProjectCommand(Arrays.asList("beta"));

        assertTrue(findFirstCommand.equals(findFirstCommand));

        FindProjectCommand findFirstCommandCopy =
                new FindProjectCommand(Arrays.asList("alpha"));
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        assertFalse(findFirstCommand.equals(1));
        assertFalse(findFirstCommand.equals(null));
        assertFalse(findFirstCommand.equals(findSecondCommand));
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
     * A Model stub that contains a fixed project list.
     */
    private class ModelStubWithProjectList extends ModelStub {
        private final UniqueProjectList uniqueProjectList = new UniqueProjectList();

        ModelStubWithProjectList(Project... projects) {
            for (Project project : projects) {
                uniqueProjectList.add(project);
            }
        }

        @Override
        public ObservableList<Project> getProjectList() {
            return uniqueProjectList.asUnmodifiableObservableList();
        }
    }
}
