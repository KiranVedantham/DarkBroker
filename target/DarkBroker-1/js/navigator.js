  $(document).ready(function(){  
	  $(document).ajaxStart(function(){
	        $("#wait").css("display", "block");
	    });
	    $(document).ajaxComplete(function(){
	        $("#wait").css("display", "none");
	    });
      $(document).on('submit', '#search-form', function(){
          
          var data = $(this).serialize();
          $.ajax({
              type : 'POST',
              url  : '/analytics/cons/12',
              data : data,
              success :  function(data){
                  $(".display").html(data);
              }
          });
          return false;
      });
      

      
      
     $(document).on('click', '.pop-button', function(){
                var data = $("#cons").val(); //Handle how you want
                if(data<1 || isNaN(data) ){
                	  $('#cons').val(""); 
                	alert("Should be number greater than Zero");
                return false;
                }else{
                $.ajax({
                    type : 'GET',
                    url: "/analytics/cons/"+$("#cons").val(),
                    data : data,
                    beforeSend: function(){
                        $("#spinner").show();
                    },
                    success :  function(data, statusText, xhr){
                    	var json = "<img src='img/service_icon_04.png'/>	<br><br><div class='text-content'> <h4>Sucesss Response Code: "+xhr.status+"</h4> <p><PRE><CODE>" +  JSON.stringify(data, null, 4)+ "</CODE></PRE> </p></div>";
                    	  $('#tab1').html(json);
                    	  $('#cons').val(""); 
                      	 $("#spinner").hide();
                    },
                   error: function (e) {
                   	 $("#spinner").hide();
                    var json = "<pre>"
                        + e.responseText + "</pre>";
                    $('#tab1').html(json);
              	  $('#cons').val(""); 

                    console.log("ERROR : ", e);
                    $("#tab1").prop("disabled", false);

                }
                });}
              });
     $(document).on('click', '#myops', function(){
    	 var $this = $(this);
         var option = $("#cons").val(); 
         var number = $this.data('id');
         if(option)
         var number = number +'/'+option;
         $.ajax({
             type : 'GET',
             url: ''+number,
             beforeSend: function(){
                 $("#spinner").show();
             },
             success :  function(data, statusText, xhr){
           	  var tab1 = "<img src='img/service_icon_04.png'/>	<br><br><div class='text-content'> <h4>Sucesss Response Code: "+xhr.status+"</h4> <p><PRE><CODE>"+JSON.stringify(data, null, 4)+"</CODE></PRE> </p></div>" ;
              $("#tab1").html(tab1);
         	 $("#spinner").hide();
         	  $('#cons').val(""); 

             },
         error: function (xhr, status, error) {
        	 $("#spinner").hide();
        	 var tab1 = "<img src='img/service_icon_04.png'/> <br><br><div class='text-content'> <h4>Failure Response Code: "+xhr.status+"</h4> <p>"+xhr.responseText+"</p> </div>" ;
              $("#tab1").html(tab1);
          	  $('#cons').val(""); 
              console.log("ERROR : ", xhr.responseText);

      }
         });
       });
     });