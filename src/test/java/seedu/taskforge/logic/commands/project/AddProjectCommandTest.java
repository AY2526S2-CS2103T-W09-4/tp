package seedu.taskforge.logic.commands.project;

import static java.util.Objects.requireNonNull;
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
import seedu.taskforge.logic.Messages;
import seedu.taskforge.logic.commands.CommandResult;
import seedu.taskforge.logic.commands.exceptions.CommandException;
import seedu.taskforge.model.AddressBook;
import seedu.taskforge.model.Model;
import seedu.taskforge.model.ReadOnlyAddressBook;
import seedu.taskforge.model.ReadOnlyUserPrefs;
import seedu.taskforge.model.person.Person;
import seedu.taskforge.model.project.Project;

public class AddProjectCommandTest {

    @Test
    public void execute_newProject_success() throws Exception {
        ModelStubAcceptingProjectAdded modelStub = new ModelStubAcceptingProjectAdded();
        Project validProject = new Project("alpha");

        CommandResult commandResult = new AddProjectCommand(validProject).execute(modelStub);

        assertEquals(
                String.format(AddProjectCommand.MESSAGE_SUCCESS, Messages.format(validProject)),
                commandResult.getFeedbackToUser());
        assertEquals(1, modelStub.projectsAdded.size());
        assertEquals(validProject, modelStub.projectsAdded.get(0));
    }

    @Test
    public void execute_duplicateProject_throwsCommandException() {
        Project validProject = new Project("alpha");
        AddProjectCommand addProjectCommand = new AddProjectCommand(validProject);
        ModelStubWithProject modelStub = new ModelStubWithProject(validProject);

        assertThrows(
                CommandException.class,
                AddProjectCommand.MESSAGE_DUPLICATE_PROJECT, () -> addProjectCommand.execute(modelStub));
    }

    @Test
    public void equals() {
        Project alpha = new Project("alpha");
        Project beta = new Project("beta");
        AddProjectCommand addAlphaCommand = new AddProjectCommand(alpha);
        AddProjectCommand addBetaCommand = new AddProjectCommand(beta);

        assertTrue(addAlphaCommand.equals(addAlphaCommand));

        AddProjectCommand addAlphaCopy = new AddProjectCommand(alpha);
        assertTrue(addAlphaCommand.equals(addAlphaCopy));

        assertFalse(addAlphaCommand.equals(1));
        assertFalse(addAlphaCommand.equals(null));
        assertFalse(addAlphaCommand.equals(addBetaCommand));
    }

    @Test
    public void toStringMethod() {
        Project project = new Project("alpha");
        AddProjectCommand addProjectCommand = new AddProjectCommand(project);
        String expected = AddProjectCommand.class.getCanonicalName() + "{toAdd=" + project + "}";
        assertEquals(expected, addProjectCommand.toString());
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
     * A Model stub that contains a single project.
     */
    private class ModelStubWithProject extends ModelStub {
        private final Project project;

        ModelStubWithProject(Project project) {
            requireNonNull(project);
            this.project = project;
        }

        @Override
        public boolean hasProject(Project project) {
            requireNonNull(project);
            return this.project.equals(project);
        }
    }

    /**
     * A Model stub that always accepts the project being added.
     */
    private class ModelStubAcceptingProjectAdded extends ModelStub {
        final ArrayList<Project> projectsAdded = new ArrayList<>();

        @Override
        public boolean hasProject(Project project) {
            requireNonNull(project);
            return projectsAdded.contains(project);
        }

        @Override
        public void addProject(Project project) {
            requireNonNull(project);
            projectsAdded.add(project);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }
}

