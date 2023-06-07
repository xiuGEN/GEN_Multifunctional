
var neonRegister = neonRegister || {};

;(function($, window, undefined)
{

	$(document).ready(function()
	{
		neonRegister.$container = $("#form_register");
		neonRegister.$steps = neonRegister.$container.find(".form-steps");
		neonRegister.$steps_list = neonRegister.$steps.find(".step");
		neonRegister.step = 'step-1'; // current step
		//添加电话号码的校验
		jQuery.validator.addMethod("phone", function(value, element, param) {
			var phonenumber=value;
			var phoneReEx=/^(((\(\d{3,4}\)|\d{3,4})?\d{7,8})|(1[3-9][0-9]{9}))$/;
			return  this.optional(element) ||phoneReEx.test(phonenumber);
		}, $.validator.format("请输入正确的电话号码"));
		jQuery.validator.addMethod("existphone", function(value, element, param) {
			var exist;
			$.ajax({
				url: "/exist/uphone",
				method: 'POST',
				async:false,
				dataType: 'json',
				data: {checkValue: value},
				success :function(respone){
					if(respone.exists == 'exists')
						exist=false;
					else if(respone.exists == 'noexists')
						exist=true;
				}
			});
			return exist;
		}, $.validator.format("手机号已注册"));



		//添加日期的校验
		jQuery.validator.addMethod("checkdate", function(value, element, param) {
			var data=value;
			var dateReEx=/^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))$/;
			return  dateReEx.test(data);
		}, $.validator.format("请输入正确的格式"));

		//对邮箱请求
		jQuery.validator.addMethod("existemail", function(value, element, param) {
			var exist;
			$.ajax({
				url: "/exist/uemail",
				method: 'POST',
				async:false,
				dataType: 'json',
				data: {checkValue: value},
				success :function(respone){
					if(respone.exists == 'exists')
						exist = false;
					else if(respone.exists == 'noexists')
						exist = true;
				}
			})
			return exist;
		}, $.validator.format("邮箱已注册"));


		jQuery.validator.addMethod("existeusname", function(value, element, param) {
			var exist;
			$.ajax({
				url: "/exist/uname",
				method: 'POST',
				async:false,
				dataType: 'json',
				data: {checkValue: value},
				success :function(respone){
					if(respone.exists == 'exists')
						exist = false;
					else if(respone.exists == 'noexists')
						exist = true;
				}
			})
			return exist;
		}, $.validator.format("用户名已注册"));


		$.extend($.validator.messages, {
			required: "不能为空",
			email:"无效的邮箱格式",
			date: "请输入有效的日期",
		});
		neonRegister.$container.validate({
			onfocusout: function(element) { $(element).valid(); },
			rules: {
				name: {
					required: true,
					rangelength:[2,7],

				},
				phone: {
					required: true,
					phone: true,
					existphone: true
				},
				email: {
					required: true,
					email: true,
					existemail: true
				},
				
				username: {
					required: true,
					rangelength:[3,7],
					existeusname:true
				},
				
				password: {
					required: true,
					rangelength:[8,15],
				},
				password2: {
					equalTo:"#password"
				},
				birthdate:{
					required: true,
					checkdate: true
				}
				
			},
			messages:{
				name:{
					rangelength:"请输入长度在2到7之间的字符"
				},
				username:{
					rangelength:"请输入长度在3到7之间的字符"
				}
				,
				password:{
					rangelength:"请输入长度在8到15之间的字符"
				}

			},
			/*触发验证方式*/
			onkeyup:false,
			highlight: function(element){ /*输入不正确的*/
				$(element).closest('.input-group').addClass('validate-has-error');
			},
			onkeyup:false,
			
			unhighlight: function(element) /*输入正确的*/
			{
				$(element).closest('.input-group').removeClass('validate-has-error');
			},
			
			submitHandler: function(ev)
			{
				$(".login-page").addClass('logging-in');
				
				neonRegister.setPercentage(30, function()
				{
					neonRegister.setPercentage(98, function()
					{
						// Send data to the server
						$.ajax({
							url: '/register',
							method: 'POST',
							dataType: 'json',
							data: {
								urealname: 		$("input#name").val(),
								uphone: 		$("input#phone").val(),
								ubirthdate: 	$("input#birthdate").val(),
								uname: 			$("input#username").val(),
								uemail: 		$("input#email").val(),
								upassword:		$("input#password").val()
							},
							error: function()
							{
								alert("注册失败,请检查网咯,请返回登录页面");
							},
							success: function(response)
							{

								neonRegister.setPercentage(100);

								if(response.register_status =='invalid'){
									alert("注册失败,请重新检查,输入的字符串是否正确");
									window.location="/register";
								}else  if(response.register_status =='success'){

									setTimeout(function () {
										// Hide the description title
										$(".login-page .login-header .description").slideUp();

										// Hide the register form (steps)
										neonRegister.$steps.slideUp('normal', function () {
											// Remove loging-in state
											$(".login-page").removeClass('logging-in');

											// Now we show the success message
											$(".form-register-success").slideDown('normal');

											// You can use the data returned from response variable
										});

									}, 500);
								}
							}
						});
					});
				});
			}
		});
	
		// Steps Handler
		neonRegister.$steps.find('[data-step]').on('click', function(ev)
		{
			ev.preventDefault();
			
			var $current_step = neonRegister.$steps_list.filter('.current'),
				next_step = $(this).data('step'),
				validator = neonRegister.$container.data('validator'),
				errors = 0;
			
			neonRegister.$container.valid();
			errors = validator.numberOfInvalids();
			
			if(errors)
			{
				validator.focusInvalid();
			}
			else
			{
				var $next_step = neonRegister.$steps_list.filter('#' + next_step),
					$other_steps = neonRegister.$steps_list.not( $next_step ),
					
					current_step_height = $current_step.data('height'),
					next_step_height = $next_step.data('height');
				
				TweenMax.set(neonRegister.$steps, {css: {height: current_step_height}});
				TweenMax.to(neonRegister.$steps, 0.6, {css: {height: next_step_height}});
				
				TweenMax.to($current_step, .3, {css: {autoAlpha: 0}, onComplete: function()
				{
					$current_step.attr('style', '').removeClass('current');
					
					var $form_elements = $next_step.find('.form-group');
					
					TweenMax.set($form_elements, {css: {autoAlpha: 0}});
					$next_step.addClass('current');
					
					$form_elements.each(function(i, el)
					{
						var $form_element = $(el);
						
						TweenMax.to($form_element, .2, {css: {autoAlpha: 1}, delay: i * .09});
					});
					
					setTimeout(function()
					{
						$form_elements.add($next_step).add($next_step).attr('style', '');
						$form_elements.first().find('input').focus();
						
					}, 1000 * (.5 + ($form_elements.length - 1) * .09));
				}});
			}
		});
		
		neonRegister.$steps_list.each(function(i, el)
		{
			var $this = $(el),
				is_current = $this.hasClass('current'),
				margin = 20;
			
			if(is_current)
			{
				$this.data('height', $this.outerHeight() + margin);
			}
			else
			{
				$this.addClass('current').data('height', $this.outerHeight() + margin).removeClass('current');
			}
		});
		
		
		// Login Form Setup
		neonRegister.$body = $(".login-page");
		neonRegister.$login_progressbar_indicator = $(".login-progressbar-indicator h3");
		neonRegister.$login_progressbar = neonRegister.$body.find(".login-progressbar div");
		
		neonRegister.$login_progressbar_indicator.html('0%');
		
		if(neonRegister.$body.hasClass('login-form-fall'))
		{
			var focus_set = false;
			
			setTimeout(function(){ 
				neonRegister.$body.addClass('login-form-fall-init')
				
				setTimeout(function()
				{
					if( !focus_set)
					{
						neonRegister.$container.find('input:first').focus();
						focus_set = true;
					}
					
				}, 550);
				
			}, 0);
		}
		else
		{
			neonRegister.$container.find('input:first').focus();
		}
		
		
		// Functions
		$.extend(neonRegister, {
			setPercentage: function(pct, callback)
			{
				pct = parseInt(pct / 100 * 100, 10) + '%';
				
				// Normal Login
				neonRegister.$login_progressbar_indicator.html(pct);
				neonRegister.$login_progressbar.width(pct);
				
				var o = {
					pct: parseInt(neonRegister.$login_progressbar.width() / neonRegister.$login_progressbar.parent().width() * 100, 10)
				};
				
				TweenMax.to(o, .7, {
					pct: parseInt(pct, 10),
					roundProps: ["pct"],
					ease: Sine.easeOut,
					onUpdate: function()
					{
						neonRegister.$login_progressbar_indicator.html(o.pct + '%');
					},
					onComplete: callback
				});
			}
		});
	});
	
})(jQuery, window);