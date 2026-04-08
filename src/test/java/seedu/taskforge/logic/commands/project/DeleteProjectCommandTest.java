package seedu.taskforge.logic.commands.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.taskforge.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.taskforge.commons.core.GuiSettings;
import seedu.taskforge.commons.core.index.Index;
import seedu.taskforge.logic.Messages;
import seedu.taskforge.logic.commands.CommandResult;
import seedu.taskforge.logic.commands.exceptions.CommandException;
import seedu.taskforge.model.Model;
import seedu.taskforge.model.ReadOnlyAddressBook;
import seedu.taskforge.model.ReadOnlyUserPrefs;
import seedu.taskforge.model.person.Person;
import seedu.taskforge.model.project.Project;
import seedu.taskforge.model.project.UniqueProjectList;

public class DeleteProjectCommandTest {

    @Test
    public void execute_validIndex_success() throws Exception {
        Project projectToDelete = new Project("alpha");
        ModelStubWithProjectList modelStub = new ModelStubWithProjectList(projectToDelete);

        CommandResult result = new DeleteProjectCommand(Index.fromOneBased(1))
                .execute(modelStub);

        assertEquals(
                String.format(DeleteProjectCommand.MESSAGE_DELETE_PROJECT_SUCCESS, projectToDelete),
                result.getFeedbackToUser());
        assertTrue(modelStub.deletedProjects.contains(projectToDelete));
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        ModelStubWithProjectList modelStub = new ModelStubWithProjectList(new Project("alpha"));
        Index outOfBoundIndex = Index.fromOneBased(2);
        DeleteProjectCommand deleteProjectCommand =
                new DeleteProjectCommand(outOfBoundIndex);

        assertThrows(
                CommandException.class,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () -> deleteProjectCommand.execute(modelStub));
    }

    @Test
    public void equals() {
        DeleteProjectCommand deleteFirstCommand =
                new DeleteProjectCommand(Index.fromOneBased(1));
        DeleteProjectCommand deleteSecondCommand =
                new DeleteProjectCommand(Index.fromOneBased(2));

        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        DeleteProjectCommand deleteFirstCopy =
                new DeleteProjectCommand(Index.fromOneBased(1));
        assertTrue(deleteFirstCommand.equals(deleteFirstCopy));

        assertFalse(deleteFirstCommand.equals(1));
        assertFalse(deleteFirstCommand.equals(null));
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        DeleteProjectCommand deleteProjectCommand =
                new DeleteProjectCommand(index);
        String expected = DeleteProjectCommand.class.getCanonicalName()
                + "{targetIndex=" + index + "}";
        assertEquals(expected, deleteProjectCommand.toString());
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
     * A Model stub that contains a fixed project list and records deleted projects.
     */
    private class ModelStubWithProjectList extends ModelStub {
        final ArrayList<Project> deletedProjects = new ArrayList<>();
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

        @Override
        public void deleteProject(Project target) {
            uniqueProjectList.remove(target);
            deletedProjects.add(target);
        }
    }
}

