function orderList(elem_id){

// get the select
	var $dd = $(elem_id);
	if ($dd.length > 0) { // make sure we found the select we were looking for

	    // save the selected value
	    var selectedVal = $dd.val();

		    // get the options and loop through them
		    var $options = $('option', $dd);
		    var arrVals = [];
		    $options.each(function(){
		        // push each option value and text into an array
		        arrVals.push({
		            val: $(this).val(),
		            text: $(this).text()
		        });
		    });
	
		    // sort the array by the value (change val to text to sort by text instead)
		    arrVals.sort(function(a, b){
		    	
		        if(a.text>b.text){
		            return 1;
		        }
		        else if (a.text==b.text){
		            return 0;
		        }
		        else {
		            return -1;
		        }
		    });
		    // loop through the sorted array and set the text/values to the options
		    for (var i = 0, l = arrVals.length; i < l; i++) {
		        $($options[i]).val(arrVals[i].val).text(arrVals[i].text);
		    }
	
		    // set the selected value back
		    $dd.val($(elem_id+" option:first").removeAttr("disabled"));
		    if(selectedVal != null){
		    	$dd.val(selectedVal);
		    } else {
		    	$dd.val(0);
		    	$(elem_id+' option').each(function(){ 
		    		   if($(this).val == 0) 
		    		         this.disabled=true;
		    		});
		    }
	}
}