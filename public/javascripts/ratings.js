$(document).ready(function() {
	
	$('.rating-input').click(function(e)
	{
		var rank = event.target.id;
		if(rank == 1){
		    	$('#1_select').attr('selected', true);
				$('#label_1').removeClass('grey');
				$('#2_select').removeAttr('selected');
				$('#label_2').addClass('grey');
				$('#3_select').removeAttr('selected');
				$('#label_3').addClass('grey');
				$('#4_select').removeAttr('selected');
				$('#label_4').addClass('grey');
				$('#5_select').removeAttr('selected');
				$('#label_5').addClass('grey');
		}
		if(rank == 2){
		    	$('#1_select').removeAttr('selected');
				$('#label_1').removeClass('grey');
				$('#2_select').attr('selected', true);
				$('#label_2').removeClass('grey');
				$('#3_select').removeAttr('selected');
				$('#label_3').addClass('grey');
				$('#4_select').removeAttr('selected');
				$('#label_4').addClass('grey');
				$('#5_select').removeAttr('selected');
				$('#label_5').addClass('grey');
		}
		if(rank == 3){
		    	$('#1_select').removeAttr('selected');
				$('#label_1').removeClass('grey');
				$('#2_select').removeAttr('selected');
				$('#label_2').removeClass('grey');
				$('#3_select').attr('selected', true);
				$('#label_3').removeClass('grey');
				$('#4_select').removeAttr('selected');
				$('#label_4').addClass('grey');
				$('#5_select').removeAttr('selected');
				$('#label_5').addClass('grey');
		}
		if(rank == 4){
		    	$('#1_select').removeAttr('selected');
				$('#label_1').removeClass('grey');
				$('#2_select').removeAttr('selected');
				$('#label_2').removeClass('grey');
				$('#3_select').removeAttr('selected');
				$('#label_3').removeClass('grey');
				$('#4_select').attr('selected', true);
				$('#label_4').removeClass('grey');
				$('#5_select').removeAttr('selected');
				$('#label_5').addClass('grey');
		}
		if(rank == 5){
		    	$('#1_select').removeAttr('selected');
				$('#label_1').removeClass('grey');
				$('#2_select').removeAttr('selected');
				$('#label_2').removeClass('grey');
				$('#3_select').removeAttr('selected');
				$('#label_3').removeClass('grey');
				$('#4_select').removeAttr('selected');
				$('#label_4').removeClass('grey');
				$('#5_select').attr('selected', true);
				$('#label_5').removeClass('grey');
		}
		
	});
});