package seedu.taskforge.logic.commands.project;

import static java.util.Objects.requireNonNull;
import static seedu.taskforge.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.taskforge.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.taskforge.commons.core.index.Index;
import seedu.taskforge.commons.util.CollectionUtil;
import seedu.taskforge.logic.Messages;
import seedu.taskforge.logic.commands.CommandResult;
import seedu.taskforge.logic.commands.exceptions.CommandException;
import seedu.taskforge.model.Model;
import seedu.taskforge.model.person.Email;
import seedu.taskforge.model.person.Name;
import seedu.taskforge.model.person.Person;
import seedu.taskforge.model.person.PersonProject;
import seedu.taskforge.model.person.PersonTask;
import seedu.taskforge.model.person.Phone;
import seedu.taskforge.model.project.Project;

/**
 * Assigns a project to an existing person in the address book.
 */
public class AssignProjectCommand extends ProjectCommand {

    public static final String SUBCOMMAND_WORD = "assign";

    public static final String MESSAGE_ASSIGN_PROJECT_SUCCESS = "Project assigned: %1$s";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " "
            + SUBCOMMAND_WORD + " INDEX "
            + PREFIX_NAME + " PROJECT_NAME";
    public static final String MESSAGE_DUPLICATE_PROJECT = "This project already exists for this person!";
    public static final String MESSAGE_PROJECT_NOT_FOUND = "The project to assign does not exist in the address book";
    public static final String MESSAGE_NOT_EDITED = "At least one project to assign must be provided";

    private final Index index;
    private final AssignProjectDescriptor assignProjectDescriptor;

    /**
     * Creates a AssignProjectCommand to assign the specified {@code Project} to the person
     * at the specified {@code Index}.
     *
     * @param index The index of the person in the filtered person list to assign the project to.
     * @param assignProjectDescriptor The project to be assigned to the person.
     */
    public AssignProjectCommand(Index index, AssignProjectDescriptor assignProjectDescriptor) {
        super();
        requireNonNull(index);
        requireNonNull(assignProjectDescriptor);
        this.index = index;
        this.assignProjectDescriptor = new AssignProjectDescriptor(assignProjectDescriptor);
    }

    /**
     * Executes the command to assign a project to the specified person.
     *
     * @param model {@code Model} which the command should operate on.
     * @return A {@code CommandResult} with the success message.
     * @throws CommandException If the specified index is invalid or if any other error occurs during execution.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        if (!assignProjectDescriptor.isProjectFieldEdited()) {
            throw new CommandException(MESSAGE_NOT_EDITED);
        }

        validateProjectsExist(assignProjectDescriptor.getProjects().orElseThrow(() ->
            new CommandException(MESSAGE_PROJECT_NOT_FOUND)), model);

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createEditedPerson(personToEdit, assignProjectDescriptor, model);

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_ASSIGN_PROJECT_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Validates that all given projects exist in the model.
     *
     * @param projects The list of projects to validate.
     * @param model The model containing the existing projects.
     * @throws CommandException If any project in the list does not exist in the model.
     */
    public static void validateProjectsExist(List<Project> projects, Model model) throws CommandException {
        for (Project project : projects) {
            if (!model.hasProject(project)) {
                throw new CommandException(MESSAGE_PROJECT_NOT_FOUND);
            }
        }
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(
            Person personToEdit,
            AssignProjectDescriptor assignProjectDescriptor,
            Model model) throws CommandException {
        assert personToEdit != null;

        Name name = personToEdit.getName();
        Phone phone = personToEdit.getPhone();
        Email email = personToEdit.getEmail();
        List<PersonTask> taskList = personToEdit.getTasks();

        List<PersonProject> newPersonProjects = new ArrayList<>(personToEdit.getProjects());
        List<Project> projectsToAssign = assignProjectDescriptor.getProjects().orElseThrow(() ->
                new CommandException(MESSAGE_PROJECT_NOT_FOUND));
        List<Project> globalProjectList = new ArrayList<>(model.getProjectList());

        // Convert Projects to PersonProjects then add to the person's project list
        for (Project project : projectsToAssign) {
            int projectIndex = globalProjectList.indexOf(project);
            if (projectIndex == -1) {
                throw new CommandException(MESSAGE_PROJECT_NOT_FOUND);
            }
            PersonProject personProject = new PersonProject(projectIndex);
            newPersonProjects.add(personProject);
        }

        checkUniquePersonProjects(newPersonProjects);

        return new Person(name, phone, email, newPersonProjects, taskList);
    }

    /**
     * Checks and returns a {@code List<PersonProject>} if there are no duplicates.
     * A {@code CommandException} is thrown if there are duplicates.
     */
    public static List<PersonProject> checkUniquePersonProjects(List<PersonProject> personProjects)
            throws CommandException {
        long distinctCount = personProjects.stream().distinct().count();
        if (distinctCount != personProjects.size()) {
            throw new CommandException(MESSAGE_DUPLICATE_PROJECT);
        }
        return personProjects;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AssignProjectCommand)) {
            return false;
        }

        AssignProjectCommand otherAssignProjectCommand = (AssignProjectCommand) other;
        return index.equals(otherAssignProjectCommand.index)
                && assignProjectDescriptor.equals(otherAssignProjectCommand.assignProjectDescriptor);
    }

    /**
     * Stores the projects to assign to the person.
     */
    public static class AssignProjectDescriptor {
        private List<Project> projects;

        public AssignProjectDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code projects} is used internally.
         */
        public AssignProjectDescriptor(AssignProjectDescriptor toCopy) {
            setProjects(toCopy.projects);
        }

        /**
         * Sets {@code projects} to this object's {@code projects}.
         * A defensive copy of {@code projects} is used internally.
         */
        public void setProjects(List<Project> projects) {
            this.projects = (projects != null) ? new ArrayList<>(projects) : null;
        }

        /**
         * Returns an unmodifiable project set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code projects} is null.
         */
        public Optional<List<Project>> getProjects() {
            return (projects != null) ? Optional.of(Collections.unmodifiableList(projects)) : Optional.empty();
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isProjectFieldEdited() {
            return CollectionUtil.isAnyNonNull(projects) && !projects.isEmpty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof AssignProjectDescriptor)) {
                return false;
            }

            AssignProjectDescriptor addProjectDescriptor = (AssignProjectDescriptor) other;
            return Objects.equals(projects, addProjectDescriptor.projects);
        }
    }
}
