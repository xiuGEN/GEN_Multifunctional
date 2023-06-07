
var neonRegister = neonRegister || {};

;(function($, window, undefined)
{

	$(document).ready(function()
	{	neonRegister.$container = $("#form_resetpassword");
		neonRegister.$steps = neonRegister.$container.find(".form-steps");
		neonRegister.$steps_list = neonRegister.$steps.find(".step");
		$.extend($.validator.messages, {
			required: "不能为空",
		});
		$("#form_resetpassword").validate({
			onfocusout: function(element) { $(element).valid(); },
			rules: {
				password: {
					required: true,
					rangelength:[8,15],
				},
				password2: {
					equalTo:"#password"
				},
			},
			messages:{
				password:{
					rangelength:"请输入长度在8到15之间的字符",
				},
				password2: {
					equalTo: "两次输入不一致",
				}

			},
			/*触发验证方式*/
			/*onkeyup:false,*/
			highlight: function(element){ /*输入不正确的*/
				$(element).closest('.input-group').addClass('validate-has-error');
			},
			
			
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
							url: '/resetpassword',
							method: 'POST',
							dataType: 'json',
							data: {
								password:		$("input#password").val()
							},
							error: function()
							{
								alert("重置失败,请检查网咯,请返回登录页面");
							},
							success: function(response)
							{

								neonRegister.setPercentage(100);

								if(response.register_status =='invalid'){
									alert("重置失败,该链接已失效,请重新获取链接");
									window.location="/login";
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
						$("#form_register").find('input:first').focus();
						focus_set = true;
					}

				}, 550);

			}, 0);
		}
		else
		{
			$("#form_register").find('input:first').focus();
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