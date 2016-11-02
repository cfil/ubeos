jQuery(document).ready(function(){
    // This button will increment the value
    $('#qtyplus').click(function(e){
        // Stop acting like a button
        e.preventDefault();
        // Get the field name
        fieldName = 'qtyval';
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
        nigthChecker();
    });
    // This button will decrement the value till 0
    $("#qtyminus").click(function(e) {
        // Stop acting like a button
        e.preventDefault();
        // Get the field name
        fieldName = 'qtyval';
        // Get its current value
        var currentVal = parseInt($('input[id='+fieldName+']').val());
        // If it isn't undefined or its greater than 0
        if (!isNaN(currentVal) && currentVal > 0) {
            // Decrement one
            $('input[id='+fieldName+']').val(currentVal - 1);
        } else {
        	if ( currentVal != 0) {
	            // Otherwise put a 1 there
	            $('input[id='+fieldName+']').val(1);
        	}
        }
        nigthChecker();
    });
});