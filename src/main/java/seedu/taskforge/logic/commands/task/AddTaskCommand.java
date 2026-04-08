package seedu.taskforge.logic.commands.task;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.taskforge.commons.core.index.Index;
import seedu.taskforge.commons.util.CollectionUtil;
import seedu.taskforge.logic.commands.CommandResult;
import seedu.taskforge.logic.commands.exceptions.CommandException;
import seedu.taskforge.model.Model;
import seedu.taskforge.model.project.Project;
import seedu.taskforge.model.task.Task;
import seedu.taskforge.model.task.exceptions.DuplicateTaskException;

/**
 * Adds a task to the task list of an existing project in the address book.
 */
public class AddTaskCommand extends TaskCommand {
    public static final String SUBCOMMAND_WORD = "add";

    public static final String MESSAGE_SUCCESS = "Task added to project: %1$s";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " "
            + SUBCOMMAND_WORD + " PROJECT_INDEX -n TASK_NAME";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists for this project!";
    public static final String MESSAGE_NOT_EDITED = "At least one task to add must be provided";
    public static final String MESSAGE_INVALID_PROJECT_INDEX = "The project index provided is invalid.";

    private final Index projectIndex;
    private final AddTaskDescriptor addTaskDescriptor;

    /**
     * Creates an AddTaskCommand to add a task to a project.
     */
    public AddTaskCommand(Index projectIndex, AddTaskDescriptor addTaskDescriptor) {
        requireNonNull(projectIndex);
        requireNonNull(addTaskDescriptor);

        this.projectIndex = projectIndex;
        this.addTaskDescriptor = new AddTaskDescriptor(addTaskDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Project> projectList = model.getProjectList();

        if (projectIndex.getZeroBased() >= projectList.size()) {
            throw new CommandException(MESSAGE_INVALID_PROJECT_INDEX);
        }

        Project projectToEdit = projectList.get(projectIndex.getZeroBased());
        Project editedProject = createEditedProject(projectToEdit, addTaskDescriptor);

        model.setProject(projectToEdit, editedProject);
        return new CommandResult(String.format(MESSAGE_SUCCESS, editedProject));
    }

    private static Project createEditedProject(Project projectToEdit,
                                               AddTaskDescriptor addTaskDescriptor) throws CommandException {
        assert projectToEdit != null;

        Project editedProject = new Project(projectToEdit.title, projectToEdit.getTasks());
        List<Task> tasksToAdd = addTaskDescriptor.getTasks()
                .orElseThrow(() -> new CommandException(MESSAGE_NOT_EDITED));

        try {
            for (Task task : tasksToAdd) {
                editedProject.getUniqueTaskList().add(task);
            }
        } catch (DuplicateTaskException dte) {
            throw new CommandException(MESSAGE_DUPLICATE_TASK);
        }

        return editedProject;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddTaskCommand)) {
            return false;
        }

        AddTaskCommand otherAddTaskCommand = (AddTaskCommand) other;
        return projectIndex.equals(otherAddTaskCommand.projectIndex)
                && addTaskDescriptor.equals(otherAddTaskCommand.addTaskDescriptor);
    }

    /**
     * Stores the details of the task that is to be added to the project.
     */
    public static class AddTaskDescriptor {
        private List<Task> tasks;

        public AddTaskDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tasks} is used internally.
         */
        public AddTaskDescriptor(AddTaskDescriptor toCopy) {
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
         * Returns an unmodifiable task list, which throws {@code UnsupportedOperationException}
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

            if (!(other instanceof AddTaskDescriptor)) {
                return false;
            }

            AddTaskDescriptor addTaskDescriptor = (AddTaskDescriptor) other;
            return Objects.equals(tasks, addTaskDescriptor.tasks);
        }
    }
}
