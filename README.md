# todolist-bot
A simple bot that lets you manage a todo-list.

You can add a new todo-entry to your list by simply entering a message.

## Supported commands

**/read** - Get your current todo-list

**/delete** - Delete your current todo-list (caution: this action is irreversible!)

**/edit** - Edit a todo-list entry. Example syntax: `/edit 3 "My fancy edited note"`, where the first parameter is the index of the note (you can find it by prior invocation of `/read`) and the second is the edited message, enclosed in single or double quotes if it contains multiple words

**/done** - Complete a todo-entry. Example syntax: `/done 2`, where the parameter is the index of the note (you can find it by prior invocation of `/read`)
