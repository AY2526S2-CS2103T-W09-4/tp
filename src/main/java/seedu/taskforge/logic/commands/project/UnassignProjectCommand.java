package seedu.taskforge.logic.commands.project;

import static java.util.Objects.requireNonNull;
import static seedu.taskforge.logic.parser.CliSyntax.PREFIX_INDEX;
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

/**
 * Unassigns a project from an existing person in the address book.
 */
public class UnassignProjectCommand extends ProjectCommand {

    public static final String SUBCOMMAND_WORD = "unassign";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " "
            + SUBCOMMAND_WORD + " INDEX "
            + PREFIX_INDEX + " PROJECT_INDEX";

    public static final String MESSAGE_UNASSIGN_PROJECT_SUCCESS = "Project unassigned: %1$s";

    public static final String MESSAGE_NOT_EDITED = "At least one project to unassign must be provided";

    public static final String MESSAGE_INVALID_PROJECT_DISPLAYED_INDEX = "The project index provided is invalid";

    private final Index index;
    private final UnassignProjectDescriptor unassignProjectDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param unassignProjectDescriptor details to edit the person with
     */
    public UnassignProjectCommand(Index index, UnassignProjectDescriptor unassignProjectDescriptor) {
        requireNonNull(index);
        requireNonNull(unassignProjectDescriptor);

        this.index = index;
        this.unassignProjectDescriptor = new UnassignProjectDescriptor(unassignProjectDescriptor);
    }

    /**
     * Executes the command to unassign a project from the specified person.
     *
     * @param model {@code Model} which the command should operate on.
     * @return A {@code CommandResult} with the success message containing the unassigned project.
     * @throws CommandException If either the person index or project index is invalid.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createEditedPerson(personToEdit, unassignProjectDescriptor);

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_UNASSIGN_PROJECT_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(
            Person personToEdit,
            UnassignProjectDescriptor unassignProjectDescriptor) throws CommandException {
        assert personToEdit != null;

        Name name = personToEdit.getName();
        Phone phone = personToEdit.getPhone();
        Email email = personToEdit.getEmail();
        List<PersonTask> taskList = personToEdit.getTasks();

        List<PersonProject> newPersonProjects = new ArrayList<>(personToEdit.getProjects());
        List<PersonProject> personProjectsToDelete = new ArrayList<>();
        List<Index> indexToDelete = unassignProjectDescriptor.getProjectsIndexes()
                .orElseThrow(() -> new CommandException(MESSAGE_NOT_EDITED));
        for (int i = 0; i < indexToDelete.size(); ++i) {
            int projectIndex = indexToDelete.get(i).getZeroBased();
            try {
                personProjectsToDelete.add(newPersonProjects.get(projectIndex));
            } catch (IndexOutOfBoundsException e) {
                throw new CommandException(MESSAGE_INVALID_PROJECT_DISPLAYED_INDEX);
            }
        }
        newPersonProjects.removeAll(personProjectsToDelete);

        return new Person(name, phone, email, newPersonProjects, taskList);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UnassignProjectCommand)) {
            return false;
        }

        UnassignProjectCommand otherUnassignProjectCommand = (UnassignProjectCommand) other;
        return index.equals(otherUnassignProjectCommand.index)
                && unassignProjectDescriptor.equals(otherUnassignProjectCommand.unassignProjectDescriptor);
    }

    /**
     * Stores the projects to unassign from the person.
     */
    public static class UnassignProjectDescriptor {
        private List<Index> indexes;

        public UnassignProjectDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code projects} is used internally.
         */
        public UnassignProjectDescriptor(UnassignProjectDescriptor toCopy) {
            setProjectsIndexes(toCopy.indexes);
        }

        /**
         * Sets {@code projects} to this object's {@code projects}.
                * A defensive copy of {@code projects} is used internally.
         */
        public void setProjectsIndexes(List<Index> indexes) {
            this.indexes = (indexes != null) ? new ArrayList<>(indexes) : null;
        }

        /**
         * Returns an unmodifiable project set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code projects} is null.
         */
        public Optional<List<Index>> getProjectsIndexes() {
            return (indexes != null) ? Optional.of(Collections.unmodifiableList(indexes)) : Optional.empty();
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isProjectFieldEdited() {
            return CollectionUtil.isAnyNonNull(indexes) && !indexes.isEmpty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof UnassignProjectDescriptor)) {
                return false;
            }

            UnassignProjectDescriptor deleteProjectDescriptor = (UnassignProjectDescriptor) other;
            return Objects.equals(indexes, deleteProjectDescriptor.indexes);
        }
    }
}
