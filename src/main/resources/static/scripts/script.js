
function auto_grow(element) {
    element.style.height = "auto";
    element.style.height = (element.scrollHeight) + "px";
}


// Add task to the form when the user clicks the add button
$('#addTaskButton').click(function() {
    var taskName = $('#newTaskInput').val();
    if (taskName) {
      var taskId = 'task' + ($('#tasksForm input[type="checkbox"]').length + 1);
      $('#tasksForm').append('<div class="form-check"><input class="form-check-input" type="checkbox" value="' + taskName + '" id="' + taskId + '" name="tasks[]"><label class="form-check-label" for="' + taskId + '">' + taskName + '</label></div>');
      $('#newTaskInput').val('');
    }
  });
  
  // Save selected tasks when the user clicks the save button
  $('#saveTasksButton').click(function() {
    var selectedTasks = $('#tasksForm input[type="checkbox"]:checked').map(function() {
        return $(this).val();
        }).get();
        console.log(selectedTasks);
        // do something with the selected tasks
        // ...
        });