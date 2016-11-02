window.fbAsyncInit = function() {
	FB.init({
	  appId      : '1510921659229025',
	  xfbml      : true,
	  cookie     : true,  // enable cookies to allow the server to access 
	                      // the session
	  version    : 'v2.5'
	});  
};

// Load the SDK asynchronously
(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/en_US/sdk.js";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));


function fb_login(){
    FB.login(function(response) {
        if (response.authResponse) {
        	useAPI();
        } else {
            console.log('User cancelled login or did not fully authorize.');
        }
    });
}

// This is called with the results from from FB.getLoginStatus().
function statusChangeCallback(response) {
  if (response.status === 'connected') {
    useAPI();
  } else {
	fb_login();
	  
  }
  
}

// This function is called when someone finishes with the Login
// Button.  See the onlogin handler attached to it in the sample
// code below.
function checkLoginState() {
  FB.getLoginStatus(function(response) {
    statusChangeCallback(response);
  });
}

	// Here we run a very simple test of the Graph API after login is
	// successful.  See statusChangeCallback() for when this call is made.
	function useAPI() {
	  var first_name;
	  var	email_fb;
	  var fb_id;
	  var profile_pic;
	  FB.api('/me?fields=id,name,email,birthday', function (response) {
			first_name = response.name;
			email_fb = response.email;
			fb_id = response.id;
			bday = response.birthday;
	      	FB.api('/me/picture?type=normal', function (response2) {
	      		
	      		profile_pic = " "+response2.data.url;
	        });
	    
	      	var formData = '{"fname":"'+ first_name+'", email:"'+ email_fb+'", id:"'+ fb_id+'", bdate:"'+ bday+'"}'; //Array 
	
		
		$.ajax({
			type: "POST",
			url: resRoute.url(),
			data: formData,
		    success: function(data, status, xhr) {
		    	window.location.href = 'http://40.118.22.163';
		    }
			
		});

	    });
	}
    
