jQuery(document).ready(function(){
    // This button will increment the value
    $('#qtyplus-adults').click(function(e){
        // Stop acting like a button
        e.preventDefault();
        // Get the field name
        fieldName = 'qtyval-adults';
        // Get its current value
        var currentVal = parseInt($('input[id='+fieldName+']').val());
        // If is not undefined
        if (!isNaN(currentVal)) {
            // Increment
            $('input[id='+fieldName+']').val(currentVal + 1);
        } else {
            // Otherwise put a 1 there
            $('input[id='+fieldName+']').val(1);
        }
        
    });
    // This button will decrement the value till 0
    $("#qtyminus-adults").click(function(e) {
        // Stop acting like a button
        e.preventDefault();
        // Get the field name
        fieldName = 'qtyval-adults';
        // Get its current value
        var currentVal = parseInt($('input[id='+fieldName+']').val());
        // If it isn't undefined or its greater than 0
        if (!isNaN(currentVal) && currentVal > 1) {
            // Decrement one
            $('input[id='+fieldName+']').val(currentVal - 1);
        } else {
            // Otherwise put a 1 there
            $('input[id='+fieldName+']').val(1);
        }

    });
    
    // This button will increment the value
    $('#qtyplus-children').click(function(e){
        // Stop acting like a button
        e.preventDefault();
        // Get the field name
        fieldName = 'qtyval-children';
        // Get its current value
        var currentVal = parseInt($('input[id='+fieldName+']').val());
        // If is not undefined
        if (!isNaN(currentVal)) {
            // Increment
            $('input[id='+fieldName+']').val(currentVal + 1);
        } else {
            // Otherwise put a 1 there
            $('input[id='+fieldName+']').val(1);
        }
        if($("#qtyval-children").val() > 0){
			$("#children-req").removeAttr("hidden");
		}
    });
    // This button will decrement the value till 0
    $("#qtyminus-children").click(function(e) {
        // Stop acting like a button
        e.preventDefault();
        // Get the field name
        fieldName = 'qtyval-children';
        // Get its current value
        var currentVal = parseInt($('input[id='+fieldName+']').val());
        // If it isn't undefined or its greater than 0
        if (!isNaN(currentVal) && currentVal > 0) {
            // Decrement one
            $('input[id='+fieldName+']').val(currentVal - 1);
        } else {
            // Otherwise put a 1 there
            $('input[id='+fieldName+']').val(0);
        }
        if($("#qtyval-children").val() < 1){
			$("#children-req").attr("hidden","hidden");
			$("#descriptionPeople").val("");
		}
    });
    
});