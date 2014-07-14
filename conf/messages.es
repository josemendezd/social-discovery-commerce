# Override default Play's validation messages

# --- Constraints
constraint.required=Obligatorio
constraint.min=Valor mínimo: {0}
constraint.max=Valor máximo: {0}
constraint.minLength=Longitud mínima: {0}
constraint.maxLength=Longitud máxima: {0}
constraint.email=Email

# --- Formats
format.date=Date (''{0}'')
format.numeric=Numérico
format.real=Real

# --- Errors
error.invalid=Valor incorrecto
error.required=Este campo es obligatorio
error.number=Se esperaba un valor numérico
error.real=Se esperaba un numero real
error.min=Debe ser mayor o igual que {0}
error.max=Debe ser menor o igual que {0}
error.minLength=La longitud mínima es de {0}
error.maxLength=La longitud máxima es de {0}
error.email=Se requiere un email válido
error.pattern=Debe satisfacer {0}

### --- play-authenticate START

# play-authenticate: Initial translations

boozology.accounts.link.success=Cuenta enlazada correctamente
boozology.accounts.merge.success=Cuentas unificadas correctamente

boozology.verify_email.error.already_validated=Su email ya ha sido validado
boozology.verify_email.error.set_email_first=Primero debe dar de alta un email.
boozology.verify_email.message.instructions_sent=Las instrucciones para validar su cuenta han sido enviadas a {0}.
boozology.verify_email.success=La dirección de email ({0}) ha sido verificada correctamente.

boozology.reset_password.message.instructions_sent=Las instrucciones para restablecer su contraseña han sido enviadas a {0}.
boozology.reset_password.message.email_not_verified=Su cuenta aún no ha sido validada. Se ha enviado un email incluyedo instrucciones para su validación. Intente restablecer la contraseña una vez lo haya recibido.
boozology.reset_password.message.no_password_account=Su usuario todavía no ha sido configurado para utilizar contraseña.
boozology.reset_password.message.success.auto_login=Su contraseña ha sido restablecida.
boozology.reset_password.message.success.manual_login=Su contraseña ha sido restablecida. Intente volver a entrar utilizando su nueva contraseña.

boozology.change_password.error.passwords_not_same=Las contraseñas no coinciden.
boozology.change_password.success=La contraseña ha sido cambiada correctamente.

boozology.password.signup.error.passwords_not_same=Las contraseñas no coinciden.
boozology.password.login.unknown_user_or_pw=Usuario o contraseña incorrectos.

boozology.password.verify_signup.subject=PlayAuthenticate: Complete su registro
boozology.password.verify_email.subject=PlayAuthenticate: Confirme su dirección de email
boozology.password.reset_email.subject=PlayAuthenticate: Cómo restablecer su contraseña

# play-authenticate: Additional translations

boozology.login.email.placeholder=Su dirección de email
boozology.login.password.placeholder=Elija una contraseña
boozology.login.password.repeat=Repita la contraseña elegida
boozology.login.title=Entrar
boozology.login.password.placeholder=Contraseña
boozology.login.now=Entrar
boozology.login.forgot.password=¿Olvidó su contraseña?
boozology.login.oauth=entre usando su cuenta con alguno de los siguientes proveedores:

boozology.signup.title=Registrarse
boozology.signup.name=Su nombre
boozology.signup.now=Regístrese
boozology.signup.oauth=regístrese usando su cuenta con alguno de los siguientes proveedores:

boozology.verify.account.title=Es necesario validar su email
boozology.verify.account.before=Antes de configurar una contraseña
boozology.verify.account.first=valide su email

boozology.change.password.title=Cambio de contraseña
boozology.change.password.cta=Cambiar mi contraseña

boozology.merge.accounts.title=Unir cuentas
boozology.merge.accounts.question=¿Desea unir su cuenta ({0}) con su otra cuenta: {1}?
boozology.merge.accounts.true=Sí, ¡une estas dos cuentas!
boozology.merge.accounts.false=No, quiero abandonar esta sesión y entrar como otro usuario.
boozology.merge.accounts.ok=OK

boozology.link.account.title=Enlazar cuenta
boozology.link.account.question=¿Enlazar ({0}) con su usuario?
boozology.link.account.true=Sí, ¡enlaza esta cuenta!
boozology.link.account.false=No, salir y crear un nuevo usuario con esta cuenta
boozology.link.account.ok=OK

# play-authenticate: Signup folder translations

boozology.verify.email.title=Verifique su email
boozology.verify.email.requirement=Antes de usar PlayAuthenticate, debe validar su email.
boozology.verify.email.cta=Se le ha enviado un email a la dirección registrada. Por favor, siga el link de este email para activar su cuenta.
boozology.password.reset.title=Restablecer contraseña
boozology.password.reset.cta=Restablecer mi contraseña

boozology.password.forgot.title=Contraseña olvidada
boozology.password.forgot.cta=Enviar instrucciones para restablecer la contraseña

boozology.oauth.access.denied.title=Acceso denegado por OAuth
boozology.oauth.access.denied.explanation=Si quiere usar PlayAuthenticate con OAuth, debe aceptar la conexión.
boozology.oauth.access.denied.alternative=Si prefiere no hacerlo, puede también
boozology.oauth.access.denied.alternative.cta=registrarse con un usuario y una contraseña.

boozology.token.error.title=Error de token
boozology.token.error.message=El token ha caducado o no existe.

boozology.user.exists.title=El usuario existe
boozology.user.exists.message=Otro usario ya está dado de alta con este identificador.

# play-authenticate: Navigation
boozology.navigation.profile=Perfil
boozology.navigation.link_more=Enlazar más proveedores
boozology.navigation.logout=Salir
boozology.navigation.login=Entrar
boozology.navigation.home=Inicio
boozology.navigation.restricted=Página restringida
boozology.navigation.signup=Dárse de alta

# play-authenticate: Handler
boozology.handler.loginfirst=Para ver ''{0}'', debe darse primero de alta.

# play-authenticate: Profile
boozology.profile.title=Perfil de usuario
boozology.profile.mail=Su nombre es {0} y su dirección de mail es {1}!
boozology.profile.unverified=sin validar - haga click para validar
boozology.profile.verified=validada
boozology.profile.providers_many=Hay {0} proveedores enlazados con su cuenta:
boozology.profile.providers_one = Hay un proveedor enlazado con su cuenta:
boozology.profile.logged=Ha entrado con:
boozology.profile.session=Su ID de usuario es {0}. Su sesión expirará el {1}.
boozology.profile.session_endless=Su ID de usuario es {0}. Su sesión no expirará nunca porque no tiene caducidad.
boozology.profile.password_change=Cambie/establezca una contraseña para su cuenta

# play-authenticate - sample: Index page
boozology.index.title=Bienvenido Play Authenticate
boozology.index.intro=Aplicación de ejemplo de Play Authenticate
boozology.index.intro_2=Esto es una plantilla para una sencilla aplicación con autentificación y autorización
boozology.index.intro_3=Mire la barra de navegación superior para ver ejemplos sencillos incluyendo las características soportadas de autentificación.
boozology.index.heading=Cabecera
boozology.index.details=Ver detalles

# play-authenticate - sample: Restricted page
boozology.restricted.secrets=¡Secretos y más secretos!

### --- play-authenticate END
