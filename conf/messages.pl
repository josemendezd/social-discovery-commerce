# Override default Play's validation messages

# --- Constraints
constraint.required=Wymagane
constraint.min=Minimalna wartość: {0}
constraint.max=Maksymalna wartość: {0}
constraint.minLength=Minimalna długość: {0}
constraint.maxLength=Maksymalna długość: {0}
constraint.email=Email

# --- Formats
format.date=Data (''{0}'')
format.numeric=Numeryczny
format.real=Real

# --- Errors
error.invalid=Niepoprawna wartość
error.required=To pole jest wymagane
error.number=Wymagana wartość numeryczna
error.real=Real number value expected
error.min=Musi być większe lub równe niż {0}
error.max=Musi być mniejsze lub równe niż {0}
error.minLength=Minimalna długość {0}
error.maxLength=Maksymalna długość {0}
error.email=Wymagany poprawny adres e-mail
error.pattern=Must satisfy {0}

### --- play-authenticate START

# play-authenticate: Initial translations

boozology.accounts.link.success=Konto został podłączone
boozology.accounts.merge.success=Konta zostały złączone

boozology.verify_email.error.already_validated=Twój adres został już zweryfikowany.
boozology.verify_email.error.set_email_first=Najpierw musisz podać adres e-mail.
boozology.verify_email.message.instructions_sent=Instrukcje dotyczące weryfikacji adresu zostały wysłane na adres {0}.
boozology.verify_email.success=Adres e-mail  ({0}) został poprawnie zweryfikowany.

boozology.reset_password.message.instructions_sent=Instrukcje dotyczące przywracania hasła zostały wysłane na adres {0}.
boozology.reset_password.message.email_not_verified=Twoje konto nie zostało jeszcze zweryfikowane. Na wskazany adres zostały wysłane instrukcje dotyczące weryfikacji. Dopiero po weryfikacji spróbuj przywrócić hasło w razie potrzeby.
boozology.reset_password.message.no_password_account=Dla tego konta nie ustawiono jeszcze możliwości logowania za pomocą hasła.
boozology.reset_password.message.success.auto_login=Twoje hasło zostało przywrócone.
boozology.reset_password.message.success.manual_login=Twoje hasło zostało przywrócone. Zaloguj się ponownie z użyciem nowego hasła.

boozology.change_password.error.passwords_not_same=Hasła nie są takie same.
boozology.change_password.success=Hasło zostało zmienione.

boozology.password.signup.error.passwords_not_same=Hasła nie są takie same.
boozology.password.login.unknown_user_or_pw=Nieznany użytkownik lub złe hasło.

boozology.password.verify_signup.subject=PlayAuthenticate: Zakończ rejestrację
boozology.password.verify_email.subject=PlayAuthenticate: Potwierdź adres e-mail
boozology.password.reset_email.subject=PlayAuthenticate: Jak ustalić nowe hasło

# play-authenticate: Additional translations

boozology.login.email.placeholder=Twój adres e-mail
boozology.login.password.placeholder=Podaj hasło
boozology.login.password.repeat=Powtórz hasło
boozology.login.title=Logowanie
boozology.login.password.placeholder=Hasło
boozology.login.now=Zaloguj się
boozology.login.forgot.password=Nie pamiętasz hasła?
boozology.login.oauth=lub zaloguj się z innym dostawcą:

boozology.signup.title=Rejestracja
boozology.signup.name=Imię i nazwisko
boozology.signup.now=Zarejestruj się
boozology.signup.oauth=lub zarejestruj się z innym dostawcą:

boozology.verify.account.title=Wymagana weryfikacja adresu e-mail
boozology.verify.account.before=Zanim ustawisz nowe hasło
boozology.verify.account.first=musisz zweryfikować swój adres e-mail.

boozology.change.password.title=Zmień hasło
boozology.change.password.cta=Zmień moje hasło

boozology.merge.accounts.title=Złącz konta
boozology.merge.accounts.question=Czy chcesz połączyć aktualne konto ({0}) z kontem: {1}?
boozology.merge.accounts.true=Tak, połącz oba konta
boozology.merge.accounts.false=Nie, opuść bieżącą sesję i zaloguj się jako nowy użytkownik
boozology.merge.accounts.ok=OK

boozology.link.account.title=Dołącz konto
boozology.link.account.question=Czy chcesz dołączyć konto ({0}) do swojego aktualnego konta użytkownika?
boozology.link.account.true=Tak, dołącz to konto
boozology.link.account.false=Nie, wyloguj mnie i utwórz nowe konto użytkownika
boozology.link.account.ok=OK

# play-authenticate: Signup folder translations

boozology.verify.email.title=Potwierdź adres e-mail
boozology.verify.email.requirement=Musisz potwierdzić swój adres e-mail, aby korzytać z PlayAuthenticate
boozology.verify.email.cta=Na wskazany adres została przesłana informacja. Skorzystaj z dołączonego do niej linku aby aktywować konto.

boozology.password.reset.title=Przywróć hasło
boozology.password.reset.cta=Przywróć moje hasło

boozology.password.forgot.title=Nie pamiętam hasła
boozology.password.forgot.cta=Prześlij instrukcję dot. przywracania hasła

boozology.oauth.access.denied.title=Dostęp OAuth zabroniony
boozology.oauth.access.denied.explanation=Jeśli chcesz używać PlayAuthenticate za pomocą OAuth, musisz zaakceptować połączenie.
boozology.oauth.access.denied.alternative=Jeśli wolisz tego nie robić możesz również
boozology.oauth.access.denied.alternative.cta=zarejestrować się podając nazwę użytkownika i hasło

boozology.token.error.title=Błąd tokena
boozology.token.error.message=Podany token stracił ważność lub nie istnieje.

boozology.user.exists.title=Użytkownik istnieje
boozology.user.exists.message=Ten użytkownik już istnieje.

# play-authenticate: Navigation
boozology.navigation.profile=Profil
boozology.navigation.link_more=Dołącz więcej dostawców
boozology.navigation.logout=Wyloguj się
boozology.navigation.login=Zaloguj się
boozology.navigation.home=Strona główna
boozology.navigation.restricted=Strona zastrzeżona
boozology.navigation.signup=Zarejestruj się

# play-authenticate: Handler
boozology.handler.loginfirst=Musisz się zalogować, aby uzyskać dostęp do strony ''{0}''

# play-authenticate: Profile
boozology.profile.title=Profil użytkownika
boozology.profile.mail=Nazywasz się {0} a twój e-mail to {1}!
boozology.profile.unverified=Niezweryfikowany - kliknij
boozology.profile.verified=zweryfikowany
boozology.profile.providers_many=Dostawcy podłączeni do Twojego konta ({0}):
boozology.profile.providers_one = Jedyny dostawca podłączony do Twojego konta:
boozology.profile.logged=Do obecnego zalogowania użyto:
boozology.profile.session=ID tego konta to {0} a jego sesja wygaśnie {1}
boozology.profile.session_endless=ID tego konta to {0}, jego sesja nie wygasa automatycznie
boozology.profile.password_change=Zmień lub ustaw hasło dla tego konta

# play-authenticate - przykład: Index page
boozology.index.title=Witaj w Play Authenticate
boozology.index.intro=Przykład Play Authenticate
boozology.index.intro_2=Oto szablon prostej aplikacji wykorzystującej Play Authenticate.
boozology.index.intro_3=Skorzystaj z powyższej nawigacji aby przetestować działanie autentykacji.
boozology.index.heading=Nagłówek
boozology.index.details=Szczegóły

# play-authenticate - przykład: Restricted page
boozology.restricted.secrets=Tajemnice, tajemnice, tajemnice... Wszędzie tajemnice!

### --- play-authenticate END