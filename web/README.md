# Message Board

S použitím tohoto templatu, vytvoř formulář, který bude postovat zprávy na message board.

## Požadavky
Vytvoř formulář, který bude mít input pro zadání jména, textarea pro zadání zprávy a tlačítko pro odeslání formuláře.

Input pro jmnéno musí mít následující atributy:
- `id="message-form"`
- `action="https://meow-board-3df096d3e21f.herokuapp.com/messages"`
- `method="post"`

Textarea pro zprávu musí mít následující atributy:
- `name="message"`

Button pro odeslání formuláře musí mít následující atributy:
- `type="submit"`

Zprávy se zobrazí jako seznam. Te si můžeš upravit podle libosti, pouze je třeba dodržet konevenci: 
 - celý seznam musí mít id `messages`
 - každá zpráva musí mít třídu `message`
 - čas zprávy musí být v elementu s třídou `time`
 - jméno autora musí být v elementu s třídou `name`
 - text zprávy musí být v elementu s třídou `text`


Vychzeje z tohoto templatu:
```html
<!doctype html>
<html>
    <head>
        <title>Message Board</title>
        <meta charset="UTF-8">
    </head>

    <body>
    
        <!-- form will be here -->
        
        <ul id="messages">
            <!-- This is the template message -->
            <li class="message" style="display: none">
                <i class="message-time"></i>
                <strong class="message-author"></strong>
                <p class="message-text"></p>
            </li>
        </ul>

    </body>

    <!-- BE CAREFUL NOT TO DELETE THE SCRIPT -->
    <script>
        const messagesPath = 'https://meow-board-3df096d3e21f.herokuapp.com/messages';

        /*
         Function to add a new message to the list
         */
        function addMessage(time, author, text) {

            // Get the template li element
            const template = document.querySelector('#messages .message');

            // Clone the template
            const newMessage = template.cloneNode(true);
            newMessage.style.display = ''; // Show the new message

            // Populate the cloned element with data
            newMessage.querySelector('.message-time').textContent = time;
            newMessage.querySelector('.message-author').textContent = author;
            newMessage.querySelector('.message-text').textContent = text;

            // Append the new message to the list
            document.getElementById('messages').appendChild(newMessage);
        }

        function getMessages() {
            fetch(messagesPath)
                    .then(response => response.json())
                    .then(messages => messages.forEach(message => addMessage(message.time, message.author, message.message)))
                    .catch(error => console.error(error));
        }

        function postMessages(event) {
            fetch(messagesPath, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ author: event.target.author.value, message: event.target.message.value })
            })
                    .then(response => {
                        if (response.ok) {
                            return response.json()
                        } else {
                            console.error('Error sending message');
                        }
                    })
                    .then(message => {
                        addMessage(message.time, message.author, message.message)
                        event.target.author.value = '';
                        event.target.message.value = '';
                    })
                    .then(error => console.error(error));
        }

        document.getElementById('message-form')
                .addEventListener('submit', (event) => {
                    event.preventDefault();
                    postMessages(event);
                });

        window.onload = getMessages;
    </script>
</html>

```
