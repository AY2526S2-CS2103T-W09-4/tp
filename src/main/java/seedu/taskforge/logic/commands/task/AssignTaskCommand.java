package seedu.taskforge.logic.commands.task;

import static java.util.Objects.requireNonNull;
import static seedu.taskforge.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.taskforge.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
import seedu.taskforge.model.task.Task;

/**
 * Assigns task(s) to an existing person in the address book.
 */
public class AssignTaskCommand extends TaskCommand {
    public static final String SUBCOMMAND_WORD = "assign";

    public static final String MESSAGE_SUCCESS = "Task assigned: %1$s";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " "
            + SUBCOMMAND_WORD + " INDEX "
            + PREFIX_NAME + " TASK_NAME";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists for this person!";
    public static final String MESSAGE_NOT_EDITED = "At least one task to assign must be provided";

    private final Index index;
    private final AssignTaskDescriptor assignTaskDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param assignTaskDescriptor details to edit the person with
     */
    public AssignTaskCommand(Index index, AssignTaskDescriptor assignTaskDescriptor) {
        requireNonNull(index);
        requireNonNull(assignTaskDescriptor);

        this.index = index;
        this.assignTaskDescriptor = new AssignTaskDescriptor(assignTaskDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        List<Task> tasksToAssign = assignTaskDescriptor.getTasks()
            .orElseThrow(() -> new CommandException(MESSAGE_NOT_EDITED));
        List<PersonTask> resolvedTasksToAssign = resolveTasksWithProjectTracking(tasksToAssign, personToEdit, model);
        Person editedPerson = createEditedPerson(personToEdit, resolvedTasksToAssign);

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
            List<PersonTask> tasksToAssign) throws CommandException {
        assert personToEdit != null;

        Name name = personToEdit.getName();
        Phone phone = personToEdit.getPhone();
        Email email = personToEdit.getEmail();
        List<PersonProject> personProjectList = personToEdit.getProjects();

        List<PersonTask> newTasks = new ArrayList<>(personToEdit.getTasks());
        newTasks.addAll(tasksToAssign);
        checkUniqueTasks(newTasks);

        return new Person(name, phone, email, personProjectList, newTasks);
    }

    private static List<PersonTask> resolveTasksWithProjectTracking(List<Task> tasks, Person person, Model model)
            throws CommandException {
        List<PersonProject> assignedPersonProjects = person.getProjects();
        List<Project> allProjects = new ArrayList<>(model.getProjectList());

        List<PersonTask> resolvedTasks = new ArrayList<>();
        for (Task task : tasks) {
            PersonTask matchedTask = null;
            for (PersonProject personProject : assignedPersonProjects) {
                int projectIndex = personProject.getProjectIndex();
                if (projectIndex >= 0 && projectIndex < allProjects.size()) {
                    Project project = allProjects.get(projectIndex);
                    if (task.getProjectTitle() != null && !project.title.equals(task.getProjectTitle())) {
                        continue;
                    }
                    int taskIndex = project.getTasks().indexOf(task);
                    if (taskIndex >= 0) {
                        matchedTask = new PersonTask(projectIndex, taskIndex);
                        break;
                    }
                }
            }

            if (matchedTask == null) {
                throw new CommandException(MESSAGE_TASK_NOT_IN_ASSIGNED_PROJECTS);
            }
            resolvedTasks.add(matchedTask);
        }

        return resolvedTasks;
    }

    /**
     * Checks and returns a {@code List<Task>} if there is no duplicates.
     * A {@code CommandException} is thrown if there are duplicates.
     *
     */
    public static List<PersonTask> checkUniqueTasks(List<PersonTask> tasks) throws CommandException {
        Set<String> uniqueTaskRefs = new HashSet<>();
        boolean hasDuplicates = tasks.stream()
                .map(task -> task.getProjectIndex() + ":" + task.getTaskIndex())
                .anyMatch(key -> !uniqueTaskRefs.add(key));
        if (hasDuplicates) {
            throw new CommandException(MESSAGE_DUPLICATE_TASK);
        }
        return tasks;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AssignTaskCommand)) {
            return false;
        }

        AssignTaskCommand otherAssignTaskCommand = (AssignTaskCommand) other;
        return index.equals(otherAssignTaskCommand.index)
                && assignTaskDescriptor.equals(otherAssignTaskCommand.assignTaskDescriptor);
    }

    /**
     * Stores the tasks to add to the person.
     */
    public static class AssignTaskDescriptor {
        private List<Task> tasks;

        public AssignTaskDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tasks} is used internally.
         */
        public AssignTaskDescriptor(AssignTaskDescriptor toCopy) {
            setTasks(toCopy.tasks);
        }

        /**
         * Sets {@code tasks} to this object's {@code tasks}.
         * A defensive copy of {@code tasks} is used internally.
         */
        public void setTasks(List<Task> tasks) {
            this.tasks = (tasks != null) ? new ArrayList<>(tasks) : null;
        }

        /**
         * Returns an unmodifiable task set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tasks} is null.
         */
        public Optional<List<Task>> getTasks() {
            return (tasks != null) ? Optional.of(Collections.unmodifiableList(tasks)) : Optional.empty();
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isTaskFieldEdited() {
            return CollectionUtil.isAnyNonNull(tasks) && !tasks.isEmpty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof AssignTaskDescriptor)) {
                return false;
            }

            AssignTaskDescriptor assignTaskDescriptor = (AssignTaskDescriptor) other;
            return Objects.equals(tasks, assignTaskDescriptor.tasks);
        }
    }
}
