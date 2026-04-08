package seedu.taskforge.logic.commands.task;

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
 * Unassign task(s) from an existing person in the address book.
 */
public class UnassignTaskCommand extends TaskCommand {
    public static final String SUBCOMMAND_WORD = "unassign";

    public static final String MESSAGE_SUCCESS = "Task unassigned: %1$s";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " "
            + SUBCOMMAND_WORD + " INDEX "
            + PREFIX_INDEX + " TASK_INDEX";
    public static final String MESSAGE_INDEX_OUT_OF_BOUND = "Task index is out of bound";
    public static final String MESSAGE_NOT_EDITED = "At least one task to unassign must be provided";

    private final Index index;
    private final UnassignTaskDescriptor unassignTaskDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param unassignTaskDescriptor details to edit the person with
     */
    public UnassignTaskCommand(Index index, UnassignTaskDescriptor unassignTaskDescriptor) {
        requireNonNull(index);
        requireNonNull(unassignTaskDescriptor);

        this.index = index;
        this.unassignTaskDescriptor = new UnassignTaskDescriptor(unassignTaskDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createEditedPerson(personToEdit, unassignTaskDescriptor, model);

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(
            Person personToEdit,
            UnassignTaskDescriptor unassignTaskDescriptor, Model model) throws CommandException {
        assert personToEdit != null;

        Name name = personToEdit.getName();
        Phone phone = personToEdit.getPhone();
        Email email = personToEdit.getEmail();
        List<PersonProject> personProjectList = personToEdit.getProjects();

        List<PersonTask> newTasks = new ArrayList<>(personToEdit.getTasks());
        List<PersonTask> tasksToDelete = new ArrayList<>();
        List<Index> indexToDelete = unassignTaskDescriptor.getTasksIndexes()
                .orElseThrow(() -> new CommandException(MESSAGE_NOT_EDITED));
        for (int i = 0; i < indexToDelete.size(); ++i) {
            int taskIndex = indexToDelete.get(i).getZeroBased();
            try {
                tasksToDelete.add(newTasks.get(taskIndex));
            } catch (IndexOutOfBoundsException e) {
                throw new CommandException(MESSAGE_INDEX_OUT_OF_BOUND);
            }
        }

        newTasks.removeAll(tasksToDelete);

        return new Person(name, phone, email, personProjectList, newTasks);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UnassignTaskCommand)) {
            return false;
        }

        UnassignTaskCommand otherUnassignTaskCommand = (UnassignTaskCommand) other;
        return index.equals(otherUnassignTaskCommand.index)
                && unassignTaskDescriptor.equals(otherUnassignTaskCommand.unassignTaskDescriptor);
    }

    /**
     * Stores the tasks to delete from the person.
     */
    public static class UnassignTaskDescriptor {
        private List<Index> indexes;

        public UnassignTaskDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tasks} is used internally.
         */
        public UnassignTaskDescriptor(UnassignTaskDescriptor toCopy) {
            setTasksIndexes(toCopy.indexes);
        }

        /**
         * Sets {@code tasks} to this object's {@code tasks}.
         * A defensive copy of {@code tasks} is used internally.
         */
        public void setTasksIndexes(List<Index> indexes) {
            this.indexes = (indexes != null) ? new ArrayList<>(indexes) : null;
        }

        /**
         * Returns an unmodifiable task set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tasks} is null.
         */
        public Optional<List<Index>> getTasksIndexes() {
            return (indexes != null) ? Optional.of(Collections.unmodifiableList(indexes)) : Optional.empty();
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isTaskFieldEdited() {
            return CollectionUtil.isAnyNonNull(indexes);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof UnassignTaskDescriptor)) {
                return false;
            }

            UnassignTaskDescriptor unassignTaskDescriptor = (UnassignTaskDescriptor) other;
            return Objects.equals(indexes, unassignTaskDescriptor.indexes);
        }
    }
}
