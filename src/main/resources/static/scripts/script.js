const titleInputBox = $('#titleNoteInput');
const textNoteInputBox = $('#noteTextInput');
const taskForm = document.getElementById("tasksForm");
const taskTitle = document.getElementById("TaskTitle");
const notesContainer = document.getElementById("notesContainer");
const savingStatus = document.getElementById("status-spinner");
const addImageModal = document.getElementById('addImageModal')
const labelPopover = document.getElementById('labeladder-popover')
const popoverInput = labelPopover.querySelector(".newLabelInput");
const restoreButtons = document.querySelectorAll(".restore-note-button");
const permaDeleteButtons = document.querySelectorAll(".delete-permanent-note-button");
let notesAndTasks = document.querySelectorAll('.noteCard, .taskCard');


const restoreAll = document.querySelectorAll(".restore-all-button");
const deleteAll = document.querySelectorAll(".delete-all-button");

const labelPopoverCard = document.getElementById('labeladder-popover-card')

let allDraggies = {};

let allTaskContainers = [];
let allTaskHelpers = [];

const monthList = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

addNoCardNotification();

function addNoCardNotification(){
    let notes = document.querySelectorAll(".appCard");
    if(notes.length === 0){
        notesContainer.classList.add("notification-square");
    }else{
        notesContainer.classList.remove("notification-square");
    }
}



document.querySelectorAll(".helper-task-dropper").forEach(item => {
    hideTaskHelpers(item);
})

function hideTaskHelpers(item) {
    $(item).hide();
    allTaskHelpers.push(item);
}

function auto_grow(element) {
    element.style.height = "auto";
    element.style.height = (element.scrollHeight) + "px";
}

var $grid = $('#notesContainer').packery({
    itemSelector: '.col',
    // columnWidth helps with drop positioning
    columnWidth: 240,
    //TODO: poubo da se zacuvuva

});

$grid.imagesLoaded().progress(function () {
    $grid.packery("shiftLayout");
});
$grid.find('.col').each(function (i, gridItem) {
    bindDraggie(gridItem);
});

function bindDraggie(gridItem) {
    let handle = gridItem.querySelectorAll(".card-title, .card-img-top, .card-text");
    var draggie = new Draggabilly(gridItem, {handle: handle});

    let id = gridItem.children[0].getAttribute("data-id");


    allDraggies[id] = draggie;
    if (gridItem.children[0].getAttribute("is-pinned") === "true") {
        console.log(allDraggies[id]);
        // $grid.packery("stamp",$(gridItem));
        allDraggies[id].disable();
    }

    console.log(draggie);

    // bind drag events to Packery
    $grid.packery('bindDraggabillyEvents', draggie);
}


function orderItems() {
    let order = []
    $grid.packery('getItemElements').forEach(col => {
        order.push(col.querySelector(".appCard").getAttribute("data-id"));
    });
    let data = {};

    data['order'] = order;
    console.log(data);
    debounce(() => {
        $.ajax({
            url: 'orderCards',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(data),
            success: (dataP) => {
                savingStatus.hidden = true;


            }, error: (jqXhr) => {
                console.log(jqXhr);
            }

        })
    }, 500);
}


let debounceTimer;

const debounce = (callback, time) => {
    window.clearTimeout(debounceTimer);
    debounceTimer = window.setTimeout(callback, time);
};

$grid.on('dragItemPositioned', orderItems);


var drake = dragula({
    revertOnSpill: true,
});

window.addEventListener("load", () => {
    document.querySelectorAll(".taskListView").forEach(task => {
        addTaskContainers(task);
    })
})

function addTaskContainers(task) {
    drake.containers.push(task);
    allTaskContainers.push(task);
}

// Initialize Dragula


// Add event listeners to handle drag-and-drop events
drake.on("drag", (el, source) => {
    console.log("ovde");
    allTaskHelpers.forEach(item => {
        $(item).fadeIn("fast");
    })

});

drake.on("dragend", (el) => {

    allTaskHelpers.forEach(item => {
        $(item).fadeOut("fast");
    })

});

drake.on("drop", (el, target, source, sibling) => {
    allTaskHelpers.forEach(item => {
        $(item).fadeOut("fast");
    })
    let data = {};

    let taskId = $(el).children()[0].getAttribute("name");

    let siblingId = null
    let siblingChildren = $(sibling).children();

    if (siblingChildren.length > 0) {
        siblingId = siblingChildren[0].getAttribute("name");
        data["siblingExists"] = true;
        data["siblingId"] = siblingId;
    } else {
        data["siblingExists"] = false;
    }
    let cardSource = source.parentElement.parentElement.parentElement.getAttribute("data-id")
    let cardTarget = target.parentElement.parentElement.parentElement.getAttribute("data-id");

    data["taskId"] = taskId;
    data["cardSource"] = cardSource;
    data["cardTarget"] = cardTarget;

    savingStatus.hidden = false;

    $.ajax({
        url: 'changeCardOfTask',
        type: 'POST',
        dataType: 'json',
        data: JSON.stringify(data),
        success: (dataP) => {
            savingStatus.hidden = true;


        }, error: (jqXhr) => {
            console.log(jqXhr);
        }

    });


    // Do something with the dropped element, like reordering or updating data
});

console.log(drake);

document.querySelectorAll(".add-color-button").forEach(colorButton => {
    addColorButtonListener(colorButton);
})

function addColorButtonListener(colorButton) {
    let colorSelector = new bootstrap.Popover(colorButton, {
        container: 'body',
        placement: 'bottom',
        fallback: 'bottom',
        html: true,
        trigger: 'click',
        customClass: 'color-popover',
        content: document.getElementById('colorpopover-content'),
    });
    let popovercont = document.getElementById('colorpopover-content');

    document.addEventListener("click", () => {
        colorSelector.hide();
    });
    colorButton.addEventListener('show.bs.popover', () => {
        colorButton.classList.add('focused-color-button');
    });
    colorButton.addEventListener('hide.bs.popover', () => {
        colorButton.classList.remove('focused-color-button');
    });

}


// document.querySelectorAll(".add-label-button").forEach(addButton => {
//     let labelSelector = new bootstrap.Popover(addButton, {
//         container: 'body',
//         placement: 'bottom',
//         fallback: 'bottom',
//         html: true,
//         trigger: 'click',
//         customClass: 'add-label-popover',
//         content: labelPopover,
//     })
//
//     let popoverInput = labelPopover.querySelector("input");
//     let allActive = [];
//
//
//     addButton.addEventListener("show.bs.popover", (e) => {
//
//         addButton.parentElement.querySelectorAll(".label-pill").forEach(pill => {
//             allActive.push(pill.getAttribute("name"));
//         })
//         popoverInput.setAttribute("data-id", addButton.getAttribute("data-id"));
//         labelPopover.querySelectorAll(".labelCheckmark").forEach(labelCheckmark => {
//             labelCheckmark.checked = allActive.includes(labelCheckmark.name);
//         })
//         labelPopover.querySelectorAll(".remove-label-button").forEach(removeButton => {
//             addRemoveButtonOnClick(removeButton);
//         })
//     })
//
//     addButton.addEventListener("hide.bs.popover", (e) => {
//         allActive = [];
//     });
//
//     //TODO: ko ce gi stegnis odma da se pojavi
//     document.addEventListener("click", (e) => {
//         let $target = $(e.target);
//
//         if ($target.parents("#labeladder-popover-card").length !== 1) {
//             labelSelector.hide();
//             popoverInput.value = "";
//         }
//     })
//
//
// })

// TODO: da se zacuvuva na karticki labelite
///////////////////////////////////////////////////////////////////////////////////////////


document.querySelectorAll(".add-label-button").forEach(addButton => {
    addLabelButtonListener(addButton);

})

function addLabelButtonListener(addButton) {
    let labelSelector = new bootstrap.Popover(addButton, {
        container: 'body',
        placement: 'bottom',
        fallback: 'bottom',
        html: true,
        trigger: 'click',
        customClass: 'add-label-popover',
        content: labelPopoverCard,
    })


    let allActive = [];

    let cardId = addButton.getAttribute("data-id");

    addButton.addEventListener("show.bs.popover", (e) => {

        addButton.parentElement.querySelectorAll(".label-pill").forEach(pill => {
            allActive.push(pill.getAttribute("name"));
        })

        labelPopoverCard.setAttribute("data-id", cardId);

        labelPopoverCard.querySelectorAll(".labelCheckmark").forEach(labelCheckmark => {
            labelCheckmark.checked = allActive.includes(labelCheckmark.name);
        })
        // labelPopover.querySelectorAll(".remove-label-button").forEach(removeButton => {
        //     addRemoveButtonOnClick(removeButton);
        // })
    })

    addButton.addEventListener("hide.bs.popover", (e) => {
        allActive = [];
    });

    //TODO: ko ce gi stegnis odma da se pojavi
    document.addEventListener("click", (e) => {
        let $target = $(e.target);

        if ($target.parents("#labeladder-popover-card").length !== 1) {
            labelSelector.hide();
            popoverInput.value = "";
        }
    })


}

document.querySelectorAll(".labels").forEach(addButton => {
    let labelSelector = new bootstrap.Popover(addButton, {
        container: 'body',
        placement: 'right',
        fallback: 'right',
        html: true,
        trigger: 'click',
        customClass: 'add-label-popover',
        content: labelPopover,
    })

    let allActive = [];


    addButton.addEventListener("show.bs.popover", (e) => {
        ``
        addButton.parentElement.querySelectorAll(".label-pill").forEach(pill => {
            allActive.push(pill.getAttribute("name"));
        })
        popoverInput.setAttribute("data-id", addButton.getAttribute("data-id"));
        labelPopover.querySelectorAll(".labelCheckmark").forEach(labelCheckmark => {
            labelCheckmark.checked = allActive.includes(labelCheckmark.name);
        })
        labelPopover.querySelectorAll(".remove-label-button").forEach(removeButton => {
            addRemoveButtonOnClick(removeButton);
        })
    })

    addButton.addEventListener("hide.bs.popover", (e) => {
        allActive = [];
    });

    //TODO: ko ce gi stegnis odma da se pojavi
    document.addEventListener("click", (e) => {
        let $target = $(e.target);

        if ($target.parents("#labeladder-popover").length !== 1) {
            labelSelector.hide();
            popoverInput.value = "";
        }
    })

});

///////////////////////////////////////////////////////////////////////////////////////////

function addRemoveButtonOnClick(removeButton) {
    removeButton.addEventListener("click", () => {
        let data = {};

        data['id'] = removeButton.value;
        $.ajax({
            url: 'removeLabel',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(data),

            success: (dataP) => {
                console.log(dataP)
                document.querySelectorAll(".label-pill").forEach(pill => {

                    if (pill.getAttribute("name") === removeButton.value) {
                        $(pill).fadeOut("fast", () =>
                            $grid.packery("shiftLayout")
                        );
                    }
                })
                labelPopoverCard.querySelectorAll(".labelCheckmark").forEach(checkmark => {
                    if (checkmark.getAttribute("name") === removeButton.value) {
                        $(checkmark.parentElement.parentElement).fadeOut("fast");
                    }
                })
                removeButton.parentElement.remove();

            }, error: (jqXhr) => {
                console.log(jqXhr);
            }

        })
    })
}

document.querySelectorAll(".newLabelInput").forEach(input => {

    input.addEventListener("keydown", (e) => {

        if (e.key === "Enter") {
            savingStatus.hidden = false;

            let data = {};

            let name = e.target.value;
            data['name'] = name;

            $.ajax({
                url: 'addLabel',
                type: 'POST',
                dataType: 'json',
                data: JSON.stringify(data),

                success: (dataP) => {

                    let id = dataP[0];
                    let name = dataP[1];

                    let template = getLabelHTML(id, name);
                    let popoverTemplate = getLabelPopoverNewEntryHTML(id, name);

                    labelPopover.children[0].children[1].insertAdjacentHTML('afterbegin', template)


                    labelPopoverCard.children[0].children[0].insertAdjacentHTML('afterbegin', popoverTemplate)

                    addLabelCheckmarkEvent(labelPopoverCard.children[0].children[0].querySelectorAll(".labelCheckmark").item(0));

                    labelPopover.querySelectorAll(".remove-label-button").forEach(button=>{
                        addRemoveButtonOnClick(button);
                    })

                    $grid.packery('shiftLayout');
                    savingStatus.hidden = true;
                    popoverInput.value = "";


                }, error: (jqXhr) => {
                    console.log(jqXhr);
                }

            })
        }
    })
})

function getLabelHTML(id, name) {
    let template = `<li class="list-group-item">
                    <label>
                        
                         <span>${name}</span>

                           </label>
                           <button class="btn remove-label-button" value="${id}">
                               <i class="fa-solid fa-trash"></i>
                           </button>
                          </li>`

    return template;
}

function getLabelPopoverNewEntryHTML(id, name) {
    let template = ` <li class="list-group-item">
                          <label>
                              <input type="checkbox"
                                     class="form-check-input labelCheckmark"
                                     name="${id}"
                              >
                              <span>${name}</span>
                          </label>
                      </li>`;
    return template;
}

document.querySelectorAll(".labelCheckmark").forEach(label => {
    addLabelCheckmarkEvent(label);
})

function addLabelCheckmarkEvent(label) {
    label.addEventListener("change", () => {
        savingStatus.hidden = false;
        console.log(label);
        let data = {};

        let labelId = label.getAttribute("name");
        let container = label.parentElement.parentElement.parentElement.parentElement.parentElement;

        let cardId = container.getAttribute("data-id");


        data["labelId"] = labelId;
        data["cardId"] = cardId;
        data["checked"] = label.checked;

        console.log(data);

        $.ajax({
            url: 'checkLabel',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(data),
            success: (dataP) => {
                savingStatus.hidden = true;
                console.log(dataP)
                if (label.checked) {
                    let name = dataP[0];
                    let id = dataP[1];
                    let template = $(getLabelPillHTML(id, name));

                    console.log(template);

                    document.querySelectorAll(".appCard").forEach(card => {

                        if (card.getAttribute("data-id") === cardId) {

                            addFilterByLabelEvent(template[0]);
                            template.hide().appendTo(card.querySelector(".label-pill-container")).fadeIn("fast", () => {
                                $grid.packery("shiftLayout");
                            });
                        }
                    })
                } else {
                    document.querySelectorAll(".appCard").forEach(card => {
                        if (card.getAttribute("data-id") === cardId) {

                            card.querySelectorAll(".label-pill").forEach(pill => {

                                if (pill.getAttribute("name") === labelId) {
                                    pill.remove();
                                }
                            });

                        }
                    })
                }


            }, error: (jqXhr) => {
                console.log(jqXhr);
            }

        });

    })
}

function getLabelPillHTML(id, name) {
    let template = `
                        <div class="label-pill" name="${id}"> 
                            ${name}
                        </div>
                   `;
    return template;
}

// function makeNewLabelCheckmark(){
//     let
// }
// Add task to the form when the user clicks the add button
$('#addTaskButton').click(function () {
    let taskName = $('#newTaskInput').val();
    if (taskName) {
        let taskId = 'task' + ($('#tasksForm input[type="checkbox"]').length + 1);
        $('#tasksForm > .modal-body').append('<div class="form-check mt-2"><input class="form-check-input" type="checkbox" value="' + taskName + '" id="' + taskId + '" name="tasks[]"><label class="form-check-label" for="' + taskId + '">' + taskName + '</label></div>');
        $('#newTaskInput').val('');
    }

});

// Save selected tasks when the user clicks the save button
$('#saveTasksButton').click(function () {
    // let allTasks = $('#tasksForm input[type="checkbox"]').map(function () {
    //     return $(this).val();
    // }).get();
    savingStatus.hidden = false;

    let allTasks = {}
    $('#tasksForm input[type="checkbox"]').each((i, x) => {

        allTasks[$(x).val()] = (($(x).prop('checked')));
    });

    console.log(allTasks);

    // let fd = new FormData(taskForm);

    let data = {};
    // data['form'] = fd.entries();
    data['title'] = taskTitle.value;
    data['allTasks'] = allTasks;


    $.ajax({
        url: 'giveTask', type: 'POST', dataType: 'json', data: JSON.stringify(data),

        success: (dataP) => {
            console.log(dataP)
            let tCardObj = dataP[0];

            let cardId = tCardObj['id'];
            let title = tCardObj['title'];
            let taskArray = tCardObj['tasks'];

            let template = makeTaskCard(cardId, title, taskArray);

            let element = $($.parseHTML(template));

            toBinButton(element.find('.delete-note-button')[0]);
            toArchiveButton(element.find('.archive-note-button')[0]);
            bindDraggie(element[0]);

            addColorButtonListener(element[0].querySelector(".add-color-button"));
            addLabelButtonListener(element[0].querySelector(".add-label-button"));

            hideTaskHelpers(element[0].querySelector(".helper-task-dropper"));

            element[0].querySelectorAll(".card-title, .card-text, .card-task").forEach(content => {
                editContentEvent(content);
            })
            //TODO: bug ko ce imas selektirano na inputo i ne se pojavuva posle
            addTaskContainers(element[0].querySelector(".taskListView"));

            element[0].querySelectorAll(".taskCheckmark").forEach(elem => {
                changeCompletionOfTask(elem);
            })
            element[0].querySelectorAll(".delete-task-button").forEach(elem => {
                addDeleteTaskEvent(elem);
            })

            taskInputEvent(element[0].querySelector(".add-new-task-input"));
            addNewTaskButtonEvent(element[0].querySelector(".add-new-task-button"));

            addPinEvent(element[0].querySelector(".pin"));

            $grid.prepend(element[0]).packery('prepended', element[0]);

            savingStatus.hidden = true;
            addNoCardNotification();
            notesAndTasks = document.querySelectorAll('.noteCard, .taskCard');


        }, error: (jqXhr) => {
            console.log(jqXhr);
        }

    })


});


function makeTaskCard(id, title, taskArray) {
    const date = new Date();
    let day = date.getDate();
    let month = date.getMonth();
    let year = date.getFullYear();

    let template = `<div class="col mb-3 " >
        <div class="card appCard position-relative taskCard "
             
            data-id=${id}
            original-c="rgb(185, 86, 185)"
            original-brighter="rgb(255,122,255)"
            original-background="rgba(129,60,129,0.1)"
            style="width: 14rem;
            --c:rgb(185, 86, 185);
            --brighter:rgb(255,122,255);
            --backgroundC:rgba(129,60,129,0.1)"
             >
            
             <div class="card-body">
                <h5 class="card-title">${title}</h5>
                
                    <p class="card-text">
                    <form>
                        <ul class="list-group list-group-flush taskListView">
                        `
    for (let i = 0; i < taskArray.length; i++) {
        template += `<li class="list-group-item task-li ">
                <input type="checkbox" ${taskArray[i]["isCompleted"] ? "checked" : ""}
                       class="form-check-input taskCheckmark"
                       name="${taskArray[i]["id"]}"
                >
                    <label class="form-check-label card-task"

                     
                           th:attr="data-id=${taskArray[i]["id"]}">
                           ${taskArray[i]["text"]}
                    </label>
                    <div class="delete-task-button" name="${taskArray[i]["id"]}">
                        <i class="fa-solid fa-x"></i>
                    </div>
            </li>`
    }


    template += `
                        </ul>
                        <div class="helper-task-dropper">
                            Drag task here
                        </div>
                    </form>
                    <input type="text" class="add-new-task-input" name="${id}">
                    <button class="add-new-task-button">Add new Task</button>
                    </p>
                
                <div class="label-container">
                    <div class="label-pill-container">
                          
                    </div>
                    <button class="btn add-label-button" data-id="${id}">
                        <i class="fa-solid fa-plus"></i>
                    </button>
                </div>
    
    
            </div>
    
            <div class="time-last-modification">
                <span >${day}</span>
                <span >${monthList[month]}</span>,
                <span >${year}</span>
    
            </div>
            <div class="d-flex flex-row " style="height:50px">
                <button class="flex-grow-1 btn p-2 text-center add-color-button"
                        value="${id}">
                    <i class="fa-solid fa-brush"></i>
                </button>
                <button class="flex-grow-1 btn  p-2 text-center add-image-button"
                        data-bs-toggle="modal"
                        data-bs-target="#addImageModal" value="${id}">
                    <i class="fa-solid fa-image"></i>
                </button>
                <button class="flex-grow-1 btn btn-warning rounded-0 p-2 text-white text-center archive-note-button"
                        value="${id}">
                    <i class="fa-solid fa-box"></i>
                </button>
                <button class="flex-grow-3 btn btn-danger rounded-0 p-2 text-white text-center delete-note-button"
                        value="${id}">
                    <i class="fa-solid fa-trash"></i>
                </button>
    
            </div>
    
            <div class="position-absolute top-0 end-0 me-1  ">
                <button class="btn pin mt-0 me-0">
                    <i class="fa-solid fa-map-pin"></i>
                    
                </button>
            </div>
        </div>
    </div>`;
    return template;
}

$('#saveNoteButton').click(() => {

    savingStatus.hidden = false;

    let data = {};

    data['title'] = titleInputBox.val();
    data['text'] = textNoteInputBox.val();


    console.log(titleInputBox.val());

    $('#saveNoteButton').html(`<div class="spinner-border spinner-border-sm" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>`).prop("disabled", true);


    $.ajax({
        url: 'giveNote', type: 'POST', dataType: 'json', data: JSON.stringify(data), success: (dataP) => {
            console.log(dataP)
            let noteData = dataP[0];

            let template = makeNote(noteData['title'], noteData['text'], noteData['id']);
            let element = $($.parseHTML(template));
            //TODO: fix so novite karticki

            toBinButton(element.find('.delete-note-button')[0]);
            toArchiveButton(element.find('.archive-note-button')[0]);
            bindDraggie(element[0]);

            addColorButtonListener(element[0].querySelector(".add-color-button"));
            addLabelButtonListener(element[0].querySelector(".add-label-button"));

            element[0].querySelectorAll(".card-title, .card-text").forEach(content => {
                editContentEvent(content);
            })


            addPinEvent(element[0].querySelector(".pin"));

            $grid.prepend(element[0]).packery('prepended', element[0]);


            $('#saveNoteButton').html('Save changes').prop("disabled", false);

            titleInputBox.val("");
            textNoteInputBox.val("");

            savingStatus.hidden = true;
            addNoCardNotification();
            notesAndTasks = document.querySelectorAll('.noteCard, .taskCard');


        }, error: (jqXhr) => {
            console.log(jqXhr);
        }

    })
})


//todo:istoto za task
document.querySelectorAll(".taskCheckmark").forEach(elem => {
    changeCompletionOfTask(elem);
})

function changeCompletionOfTask(elem) {
    elem.addEventListener("change", () => {
        savingStatus.hidden = false;

        let data = {}

        data['id'] = elem.name;
        data['checked'] = elem.checked;

        if (elem.checked) elem.nextElementSibling.classList.add("strike"); else elem.nextElementSibling.classList.remove("strike");


        $.ajax({
            url: 'changeStatus', type: 'POST', dataType: 'json', data: JSON.stringify(data), success: (dataP) => {
                console.log(dataP)
                savingStatus.hidden = true;

            }, error: (jqXhr) => {
                console.log(jqXhr);
            }

        })

    })
}

document.querySelectorAll(".delete-note-button").forEach(elem => {
    toBinButton(elem);
})


function toBinButton(elem) {
    elem.addEventListener("click", () => {
        savingStatus.hidden = false;

        let data = {};

        data['id'] = elem.value;
        $(elem.parentElement.parentElement.parentElement).fadeOut(200, () => {

            $.ajax({
                url: 'binCard', type: 'POST', dataType: 'json', data: JSON.stringify(data), success: (dataP) => {
                    console.log(dataP);
                    $grid.packery('remove', elem)

                        .packery('shiftLayout');
                    $(elem).remove();
                    savingStatus.hidden = true;
                    addNoCardNotification();

                }, error: (jqXhr) => {
                    console.log(jqXhr);
                }

            })
        })

    })
}

document.querySelectorAll(".archive-note-button").forEach(elem => {
    toArchiveButton(elem);
})

function toArchiveButton(elem) {
    elem.addEventListener("click", () => {
        savingStatus.hidden = false;

        let data = {};

        data['id'] = elem.value;
        $(elem.parentElement.parentElement.parentElement).fadeOut(200, () => {

            $.ajax({
                url: 'archiveCard', type: 'POST', dataType: 'json', data: JSON.stringify(data), success: (dataP) => {
                    console.log(dataP);
                    $grid.packery('remove', elem)

                        .packery('shiftLayout');
                    $(elem).remove();
                    savingStatus.hidden = true;
                    addNoCardNotification();

                }, error: (jqXhr) => {
                    console.log(jqXhr);
                }

            })
        })

    })
}

function makeNote(title, text, id) {
    const date = new Date();
    let day = date.getDate();
    let month = date.getMonth();
    let year = date.getFullYear();
    let template = `<div class="col mb-3 ">
    <div class="card appCard position-relative noteCard "
         
        data-id=${id}
        original-c="rgb(185, 86, 185)"
        original-brighter="rgb(255,122,255)"
        original-background="rgba(129,60,129,0.1)"
        style="width: 14rem;
        --c:rgb(185, 86, 185);
        --brighter:rgb(255,122,255);
        --backgroundC:rgba(129,60,129,0.1)"
         >

        <div class="card-body">
            <h5 class="card-title">${title}</h5>
            <p class="card-text" >
                ${text}
            </p>

            
            <div class="label-container">
                <div class="label-pill-container">
                    
                </div>
                <button class="btn add-label-button" data-id="${id}">
                    <i class="fa-solid fa-plus"></i>
                </button>
            </div>


        </div>

        <div class="time-last-modification">
            <span >${day}</span>
            <span >${monthList[month]}</span>,
            <span >${year}</span>

        </div>
        <div class="d-flex flex-row " style="height:50px">
            <button class="flex-grow-1 btn p-2 text-center add-color-button"
                    value="${id}">
                <i class="fa-solid fa-brush"></i>
            </button>
            <button class="flex-grow-1 btn  p-2 text-center add-image-button"
                    data-bs-toggle="modal"
                    data-bs-target="#addImageModal" value="${id}">
                <i class="fa-solid fa-image"></i>
            </button>
            <button class="flex-grow-1 btn btn-warning rounded-0 p-2 text-white text-center archive-note-button"
                    value="${id}">
                <i class="fa-solid fa-box"></i>
            </button>
            <button class="flex-grow-3 btn btn-danger rounded-0 p-2 text-white text-center delete-note-button"
                    value="${id}">
                <i class="fa-solid fa-trash"></i>
            </button>

        </div>

        <div class="position-absolute top-0 end-0 me-1  ">
            <button class="btn pin mt-0 me-0">
                <i class="fa-solid fa-map-pin"></i>
                
            </button>
        </div>
    </div>
</div>`;

    return template;

}


if (addImageModal) {
    addImageModal.addEventListener('show.bs.modal', event => {

        const button = event.relatedTarget

        const value = button.getAttribute('value')

        console.log(value);

        const inputId = addImageModal.querySelector('#addImageModalId')

        inputId.value = value;
    })
}

document.querySelectorAll(".color-circle").forEach(colordiv => {
    let originalBase = null;
    let originalLight = null;
    let originalDark = null;
    let card = null;

    colordiv.addEventListener("mouseover", () => {
        let base = colordiv.getAttribute("data-color");
        let light = colordiv.getAttribute("data-light");
        let dark = colordiv.getAttribute("data-dark");

        try {
            //mnogu brzo ako se stegni pak na boite posle menuvanje
            card = document.getElementsByClassName("focused-color-button").item(0).parentElement.parentElement
            card.style.setProperty("--c", base);
            card.style.setProperty("--brighter", light);
            card.style.setProperty("--backgroundC", dark);

            originalBase = card.getAttribute('original-c');
            originalLight = card.getAttribute('original-brighter');
            originalDark = card.getAttribute('original-background');
        } catch (e) {

        }


    })
    colordiv.addEventListener("mouseout", () => {
        card.style.setProperty("--c", originalBase);
        card.style.setProperty("--brighter", originalLight);
        card.style.setProperty("--backgroundC", originalDark);
        console.log(card);
    })

    colordiv.addEventListener("click", () => {
        savingStatus.hidden = false;
        let id = card.getAttribute("data-id");

        base = colordiv.getAttribute("data-color");
        light = colordiv.getAttribute("data-light");
        dark = colordiv.getAttribute("data-dark");
        originalBase = base;
        originalLight = light;
        originalDark = dark;

        card.setAttribute('original-c', base);
        card.setAttribute("original-brighter", light);
        card.setAttribute("original-background", dark);

        let data = {}

        data['id'] = id;
        data['base'] = base;

        $.ajax({
            url: 'changeColor', type: 'POST', dataType: 'json', data: JSON.stringify(data), success: (dataP) => {


                savingStatus.hidden = true;

            }, error: (jqXhr) => {
                console.log(jqXhr);
            }

        });
    })

});

//TODO: zacuvuvanje na redosled


//Editing


document.querySelectorAll(".card-title, .card-text, .card-task").forEach(content => {
    editContentEvent(content);
})

function editContentEvent(content) {
    content.addEventListener("dblclick", () => {

        content.classList.add("editing-title")
        content.contentEditable = true;
        content.focus();

        let type = null;
        if (content.classList.contains("card-title")) {
            type = "title";
        } else if (content.classList.contains("card-text")) {
            type = "text";
        } else {
            type = "task";
        }
        let id = null;
        if (type === "task") {
            id = content.getAttribute("data-id");
        } else {
            id = content.parentElement.parentElement.getAttribute("data-id");
        }
        let range = document.createRange();
        let sel = document.getSelection();

        let lastChild = content.childNodes[content.childNodes.length - 1];


        range.setStart(lastChild, lastChild.data ? lastChild.data.length : 0)
        range.collapse(true)

        sel.removeAllRanges()
        sel.addRange(range)
        let draggieInstance = null;
        if (type !== "task") {
            draggieInstance = allDraggies[id];
            draggieInstance.disable();
        }


        document.addEventListener("click", outOfInputClick);

        function outOfInputClick(e) {

            if (e.target !== content) {

                let text = content.innerText;
                removeFocusEdit(content);

                if (type !== "task" && draggieInstance !== null)
                    draggieInstance.enable();

                document.removeEventListener("click", outOfInputClick);

                sendEdit(id, text, type);
            }


        }

        let keys = [];
        if (type === "title" || type === "task") {
            keys = ["Enter", "Escape"];
        } else {
            keys = ["Escape"];
        }


        content.addEventListener("keydown", (e) => {

            if (keys.includes(e.key)) {

                let text = content.innerText;
                removeFocusEdit(content);

                if (type !== "task")
                    draggieInstance.enable();


                sendEdit(id, text, type);


            }
        })
    })
    content.addEventListener("input", () => {

        $grid
            .packery('shiftLayout');
    })
}

function removeFocusEdit(elem) {
    elem.classList.remove("editing-title")
    elem.contentEditable = false;
    elem.blur();
}

function sendEdit(id, text, type) {
    let data = {};

    savingStatus.hidden = false;
    data['id'] = id;
    data['text'] = text;
    data['type'] = type;

    console.log(data);
    $.ajax({
        url: 'editCard',
        type: 'POST',
        dataType: 'json',
        data: JSON.stringify(data),
        success: (dataP) => {
            savingStatus.hidden = true;


        }, error: (jqXhr) => {
            console.log(jqXhr);
        }

    });
}

document.querySelectorAll(".add-new-task-input").forEach(taskInput => {
    taskInputEvent(taskInput);
})

function taskInputEvent(taskInput) {
    $(taskInput).hide();
    taskInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
            let data = {};
            savingStatus.hidden = false;
            data["id"] = taskInput.getAttribute("name");
            data["text"] = taskInput.value;


            $.ajax({
                url: 'addTaskToCard',
                type: 'POST',
                dataType: 'json',
                data: JSON.stringify(data),
                success: (dataP) => {
                    savingStatus.hidden = true;
                    console.log(dataP);
                    let id = dataP[2];
                    let text = dataP[0];

                    let container = taskInput.parentElement.querySelector("form").children[0];

                    let template = $(getNewTaskToCardHTML(id, text));

                    changeCompletionOfTask(template.find(".taskCheckmark").get()[0]);
                    template.hide().appendTo(container)
                    template.fadeIn("fast", () => {
                        $grid.packery("shiftLayout");
                    })
                    taskInput.value = "";

                }, error: (jqXhr) => {
                    console.log(jqXhr);
                }

            });
        }
    })
}

function getNewTaskToCardHTML(id, text) {
    let template = `<li class="list-group-item ">
                    <input type="checkbox"
                           class="form-check-input taskCheckmark"
                           name="${id}"
                           >

                           <label class="form-check-label card-task"

                                  data-id=${id}>${text}
                           </label>
                    </li>`;

    return template;
}

document.querySelectorAll(".add-new-task-button").forEach(button => {
    addNewTaskButtonEvent(button);
})

function addNewTaskButtonEvent(button) {
    button.addEventListener("click", () => {
        let input = $(button).parent().find(".add-new-task-input");
        if (!input.is(":visible")) {
            input.show("fast", () => {
                $grid.packery("shiftLayout")
            });
        } else {
            input.hide("fast", () => {
                $grid.packery("shiftLayout")
            });
        }
    })
}

document.querySelectorAll(".delete-task-button").forEach(button => {
    addDeleteTaskEvent(button);
})

function addDeleteTaskEvent(button) {
    button.addEventListener("click", () => {
        savingStatus.hidden = false;

        let data = {};

        data["taskId"] = button.getAttribute("name");
        data["cardId"] = button.parentElement.parentElement.parentElement.parentElement.parentElement.getAttribute("data-id");

        let parent = button.parentElement;
        $(parent).fadeOut("fast", () => {
            parent.remove();
        });

        $.ajax({
            url: 'removeTaskFromCard',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(data),
            success: (dataP) => {
                savingStatus.hidden = true;


            }, error: (jqXhr) => {
                console.log(jqXhr);
            }

        });
    })
}

//taskListView
document.querySelectorAll(".pin").forEach(pin => {
    addPinEvent(pin);
});

function addPinEvent(pin) {
    pin.addEventListener("click", () => {
        savingStatus.hidden = false;

        let card = pin.parentElement.parentElement;
        let cardId = card.getAttribute("data-id");
        let data = {};
        let position = allDraggies[cardId].position;

        data['cardId'] = cardId;
        data['xPos'] = position["x"];
        data['yPos'] = position["y"];

        if (pin.classList.contains("isPinned")) {
            pin.classList.remove("isPinned");
            card.parentElement.classList.remove("pinnedCard");
            allDraggies[cardId].enable();
        } else {
            pin.classList.add("isPinned");
            card.parentElement.classList.add("pinnedCard");
            allDraggies[cardId].disable();

        }


        $.ajax({
            url: 'togglePin',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(data),
            success: (dataP) => {
                savingStatus.hidden = true;


            }, error: (jqXhr) => {
                console.log(jqXhr);
            }

        });
    })
}

document.querySelectorAll(".delete-image-button").forEach((elem) => {
    addDeleteImageEvent(elem);
})

function addDeleteImageEvent(elem) {
    elem.addEventListener("click", () => {
        savingStatus.hidden = false;
        let cardId = elem.parentElement.getAttribute("data-id");

        console.log(cardId);
        let data = {};

        data["cardId"] = cardId;

        $.ajax({
            url: 'deleteImage',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(data),
            success: (dataP) => {
                savingStatus.hidden = true;
                window.location.reload();


            }, error: (jqXhr) => {
                console.log(jqXhr);
            }

        });
    })

}

// ////////////////////////////////////////////////////////////////////////////////

// TODO: highlight searched text on new added card without reloading the page

document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.querySelector('.tasks-input');

    searchInput.addEventListener('input', function () {
        const searchTerm = searchInput.value.trim().toLowerCase();

        notesAndTasks.forEach(function (card) {
            const cardTitleElement = card.querySelector('.card-title');
            const cardContentElement = card.querySelector('.card-text');

            const cardTitle = cardTitleElement.textContent.toLowerCase();
            const cardContent = cardContentElement.textContent.toLowerCase();

            // Search within tasks if it's a task card
            if (card.classList.contains('taskCard')) {
                const taskItems = card.querySelectorAll('.list-group-item');
                let shouldDisplayCard = false; // Keep track if any task matches

                taskItems.forEach(function (taskItem) {
                    const taskTitleElement = taskItem.querySelector('.card-task');
                    const taskTitle = taskTitleElement.textContent.toLowerCase();

                    if (taskTitle.includes(searchTerm) || cardTitle.includes(searchTerm)) {
                        highlightText(taskTitleElement, searchTerm);
                        highlightText(cardTitleElement, searchTerm);
                        shouldDisplayCard = true;
                    } else {
                        unhighlightText(taskTitleElement);
                    }
                });

                card.style.display = shouldDisplayCard ? 'block' : 'none'; // Show the entire task card if any task matches
            } else {
                if (cardTitle.includes(searchTerm) || cardContent.includes(searchTerm)) {
                    card.style.display = 'block';
                    highlightText(cardTitleElement, searchTerm);
                    highlightText(cardContentElement, searchTerm);
                    $grid.packery("shiftLayout");
                } else {
                    card.style.display = 'none';
                    $grid.packery("shiftLayout");
                }
            }
        });


    });
    document.querySelectorAll(".label-pill").forEach(label => {
        addFilterByLabelEvent(label);
    })

    function highlightText(element, term) {
        const regex = new RegExp(`(${term})`, 'gi');
        const replacement = '<span class="highlighted">$1</span>';
        element.innerHTML = element.textContent.replace(regex, replacement);
    }

    function unhighlightText(element) {
        element.innerHTML = element.textContent; // Remove the highlighting
    }


});

function highlightLabel(label) {
    label.classList.add("highlight-label");
}

function unhighlightLabel(label) {
    label.classList.remove("highlight-label");
}

function addFilterByLabelEvent(label) {
    label.addEventListener("click", () => {
        let labelId = label.getAttribute("name");
        console.log(labelId);
        const notesAndTasks = document.querySelectorAll('.noteCard, .taskCard');
        console.log(notesAndTasks);
        notesAndTasks.forEach(function (card) {
            const cardTitleElement = card.querySelector('.card-title');
            const cardContentElement = card.querySelector('.card-text');


            // Search within tasks if it's a task card

            const labelsFromCard = card.querySelectorAll('.label-pill');
            let shouldDisplayCard = false; // Keep track if any task matches

            labelsFromCard.forEach(function (labelItem) {

                const labelCardId = labelItem.getAttribute("name");

                if (labelCardId === labelId) {
                    highlightLabel(labelItem);
                    shouldDisplayCard = true;
                } else {
                    unhighlightLabel(labelItem);
                }
            });

            // card.style.display = shouldDisplayCard ? 'block' : 'none'; // Show the entire task card if any task matches
            if (!shouldDisplayCard) {
                $(card).fadeOut("fast", () => {
                    $grid.packery("shiftLayout");
                });
            } else {
                $(card).fadeIn("fast", () => {
                    $grid.packery("shiftLayout");
                })
            }

        });

        document.addEventListener("click", defocusClick);

        function defocusClick(e) {

            if (e.target.classList.contains("label-pill")) {
                return;
            }

            notesAndTasks.forEach((card) => {
                card.style.display = 'block';
            })
            $grid.packery("shiftLayout");

            document.querySelectorAll(".highlight-label").forEach(highlightedLabel => {
                highlightedLabel.classList.remove("highlight-label");
            })
            document.removeEventListener("click", defocusClick);
        }
    })

}

// //////////////////////////////////////////////////////////////////////////////////

Dropzone.options.imageForm = {
    withCredentials: true,
    maxFilesize: 16384,
    maxFiles: 1,
    acceptedFiles: "image/*",
    paramName: "image",
    complete: function (file, done) {
        window.location.reload();
    }
}


// let imageDropzone=new Dropzone("#imageForm");
// console.log(imageDropzone);

restoreButtons.forEach(button => {
    button.addEventListener("click", () => {
        let data = {}

        let cardId = button.getAttribute("value");
        data["cardId"] = cardId;

        let cardElem = button.parentElement.parentElement.parentElement;

        $(cardElem).fadeOut("fast", () => {
            cardElem.remove();
            $grid.packery("shiftLayout");
        })

        $.ajax({
            url: 'restoreCard',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(data),
            success: (dataP) => {
                savingStatus.hidden = true;
                addNoCardNotification();


            }, error: (jqXhr) => {
                console.log(jqXhr);
            }

        });
    });

})
permaDeleteButtons.forEach(button => {
    button.addEventListener("click", () => {
        let data = {}

        let cardId = button.getAttribute("value");
        data["cardId"] = cardId;

        let cardElem = button.parentElement.parentElement.parentElement;

        $(cardElem).fadeOut("fast", () => {
            cardElem.remove();
            $grid.packery("shiftLayout");
        })
        //TODO: da ne se gleda + i add task vo archive i trash
        $.ajax({
            url: 'deleteCardPermanent',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(data),
            success: (dataP) => {
                savingStatus.hidden = true;
                addNoCardNotification();


            }, error: (jqXhr) => {
                console.log(jqXhr);
            }

        });
    });
});


restoreAll.forEach(button => {
    button.addEventListener("click", () => {
        document.querySelectorAll("#notesContainer>.col").forEach(card => {
            $(card).fadeOut("fast");
        });
        let data = {};
        data["type"] = button.getAttribute("value");
        //TODO: napraj da znaj dali e archive ili trash
        $.ajax({
            url: 'restoreAll',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(data),
            success: (dataP) => {
                savingStatus.hidden = true;
                addNoCardNotification();


            }, error: (jqXhr) => {
                console.log(jqXhr);
            }

        });
    })
})

deleteAll.forEach(button => {
    button.addEventListener("click", () => {
        document.querySelectorAll("#notesContainer>.col").forEach(card => {
            $(card).fadeOut("fast");
        });
        let data = {};
        data["type"] = button.getAttribute("value");
        $.ajax({
            url: 'deleteAll',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(data),
            success: (dataP) => {
                savingStatus.hidden = true;
                addNoCardNotification();


            }, error: (jqXhr) => {
                console.log(jqXhr);
            }

        });
    })
})
