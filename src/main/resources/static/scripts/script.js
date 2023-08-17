const titleInputBox = $('#titleNoteInput');
const textNoteInputBox = $('#noteTextInput');
const taskForm = document.getElementById("tasksForm");
const taskTitle = document.getElementById("TaskTitle");
const notesContainer = document.getElementById("notesContainer");
const savingStatus = document.getElementById("status-spinner");
const addImageModal = document.getElementById('addImageModal')
const labelPopover = document.getElementById('labeladder-popover')

let allDraggies = {};

let allTaskContainers = [];
let allTaskHelpers = [];

document.querySelectorAll(".helper-task-dropper").forEach(item => {
    $(item).hide();
    allTaskHelpers.push(item);
})

function auto_grow(element) {
    element.style.height = "auto";
    element.style.height = (element.scrollHeight) + "px";
}

var $grid = $('#notesContainer').packery({
    itemSelector: '.col',
    // columnWidth helps with drop positioning
    columnWidth: 80,
    gutter: 3
});

$grid.imagesLoaded().progress(function () {
    $grid.packery();
});
$grid.find('.col').each(function (i, gridItem) {
    let handle = gridItem.querySelector(".card-title");
    var draggie = new Draggabilly(gridItem, {handle: handle});

    let id = gridItem.children[0].getAttribute("data-id");

    allDraggies[id] = draggie;

    // bind drag events to Packery
    $grid.packery('bindDraggabillyEvents', draggie);
});


function orderItems() {
    console.log($grid.packery('getItemElements'));
}

$grid.on('dragItemPositioned', orderItems);


var drake = dragula({
    revertOnSpill: true,
});

window.addEventListener("load", () => {
    document.querySelectorAll(".taskListView").forEach(task => {
        drake.containers.push(task);
        allTaskContainers.push(task);
    })
})


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

    if(siblingChildren.length > 0){
        siblingId = siblingChildren[0].getAttribute("name");
        data["siblingExists"] = true;
        data["siblingId"] = siblingId;
    }else{
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
    let colorSelector = new bootstrap.Popover(colorButton, {
        container: 'body',
        placement: 'bottom',
        fallback: 'bottom',
        html: true,
        trigger: 'click',
        customClass: 'color-popover',
        content: document.getElementById('colorpopover-content'),
    })
    let popovercont = document.getElementById('colorpopover-content')

    document.addEventListener("click", () => {
        colorSelector.hide();
    })
    colorButton.addEventListener('show.bs.popover', () => {
        colorButton.classList.add('focused-color-button');
    })
    colorButton.addEventListener('hide.bs.popover', () => {
        colorButton.classList.remove('focused-color-button');
    })
})

document.querySelectorAll(".add-label-button").forEach(addButton => {
    let labelSelector = new bootstrap.Popover(addButton, {
        container: 'body',
        placement: 'bottom',
        fallback: 'bottom',
        html: true,
        trigger: 'click',
        customClass: 'add-label-popover',
        content: labelPopover,
    })

    let popoverInput = labelPopover.querySelector("input");
    let allActive = [];


    addButton.addEventListener("show.bs.popover", (e) => {

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


})

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
            console.log(e.target.value)
            console.log(e.target.getAttribute("data-id"))
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
                    labelPopover.children[0].children[1].insertAdjacentHTML('afterbegin', template)
                    addLabelCheckmarkEvent(labelPopover.children[0].children[1].querySelectorAll(".labelCheckmark").item(0));
                    $grid.packery('shiftLayout');
                    savingStatus.hidden = true;

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
                          <input type="checkbox"
                                class="form-check-input labelCheckmark"
                                name="${id}"
                             >
                         <span>${name}</span>

                           </label>
                           <button class="btn remove-label-button" value="${id}">
                               <i class="fa-solid fa-trash"></i>
                           </button>
                          </li>`

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
        let labelInput = labelPopover.querySelector("input");

        let cardId = labelInput.getAttribute("data-id");


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

                    document.querySelectorAll(".appCard").forEach(card => {

                        if (card.getAttribute("data-id") === cardId) {
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
                                    pill.parentElement.remove();
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
    let template = `<a href="/label/${id}">
                        <div class="label-pill" name="${id}"> 
                            ${name}
                        </div>
                   </a>`;
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
            savingStatus.hidden = true;


        }, error: (jqXhr) => {
            console.log(jqXhr);
        }

    })


});

//TODO: dodavanje na taskoj
//TODO: editiranje na taskoj

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
            console.log(element);
            $grid.prepend(element[0]).packery('prepended', element[0]);


            $('#saveNoteButton').html('Save changes').prop("disabled", false);

            titleInputBox.val("");
            textNoteInputBox.val("");

            savingStatus.hidden = true;


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

                }, error: (jqXhr) => {
                    console.log(jqXhr);
                }

            })
        })

    })
}

function makeNote(title, text, id) {
    let template = `<div class="col ">
        <div class="card appCard position-relative " style="width: 14rem;">

                <div class="card-body">
                    <h5 class="card-title">${title}</h5>
                    <p class="card-text" 
                       >
                    ${text}
                    </p>
                    
                  
                </div>


                <div class="d-flex flex-row ">
                    <button class="flex-grow-1 btn p-3 text-center add-color-button">
                        <i class="fa-solid fa-brush"></i>
                    </button>
                    <button class="flex-grow-1 btn  p-3 text-center add-image-button"
                    data-bs-target="#addImageModal"
                    value="${id}">
                        <i class="fa-solid fa-image"></i>
                    </button>
                    <button
                        class="flex-grow-1 btn btn-danger rounded-0 p-3 text-white text-center delete-note-button"
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

        if (type !== "task") {
            let draggieInstance = allDraggies[id];
            draggieInstance.disable();
        }


        document.addEventListener("click", outOfInputClick);

        function outOfInputClick(e) {

            if (e.target !== content) {

                let text = content.innerText;
                removeFocusEdit(content);

                if (type !== "task")
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
})


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
})

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
})

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

