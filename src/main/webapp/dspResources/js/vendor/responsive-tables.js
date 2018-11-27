var switched = false;
var updateTables = function ()
{
    $("table.responsive").each(function (i, element)
    {
        if ($(element).width() > $(element).parent().innerWidth() && !switched)
        {
            switched = true;
            splitTable($(element));
        } else if ($(element).width() <= $(element).parent().innerWidth() && switched)
        {
            switched = false;
            unsplitTable($(element));
        }
    });
    return true;
};

updateTables();
$(window).on("redraw", function () {
    updateTables();
}); // An event to listen for
$(window).on("resize", updateTables);


function splitTable(original)
{
    original.wrap("<div class='table-wrapper' />");

    var copy = original.clone();
    copy.find("td:not(:first-child), th:not(:first-child)").css("display", "none");
    copy.removeClass("responsive");
    original.closest(".table-wrapper").append(copy);
    copy.wrap("<div class='pinned' />");
    original.wrap("<div class='scrollable' />");
    setCellHeights(original, copy);
    original.find('td:first-child, th:first-child').css('display', 'none');
}

function unsplitTable(original) {
    original.closest(".table-wrapper").find(".pinned").remove();
    original.unwrap();
    original.unwrap();
    original.find('td:first-child, th:first-child').css('display', 'table-cell');
}

function setCellHeights(original, copy) {
    var tr = original.find('tr'),
            tr_copy = copy.find('tr'),
            heights = [];

    tr.each(function (index) {
        var self = $(this),
                tx = self.find('th, td');

        tx.each(function () {
            var height = $(this).outerHeight(true);
            heights[index] = heights[index] || 0;
            if (height > heights[index])
                heights[index] = height;
        });

    });

    tr_copy.each(function (index) {
        $(this).height(heights[index]);
    });
}