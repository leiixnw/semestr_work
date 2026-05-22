function getCsrfConfig() {
    const tokenEl = document.querySelector("meta[name='_csrf']");
    const headerEl = document.querySelector("meta[name='_csrf_header']");

    if (!tokenEl || !headerEl) {
        return { headers: {} };
    }

    return {
        token: tokenEl.getAttribute("content"),
        header: headerEl.getAttribute("content")
    };
}

function sendApplication(proposalId) {
    const commentText = document.getElementById('comment').value;
    const resultDiv = document.getElementById('apply-result');
    const csrf = getCsrfConfig();

    if (!resultDiv) return;

    resultDiv.innerHTML = "Отправка...";

    fetch('/api/applications/apply', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrf.header]: csrf.token
        },
        body: JSON.stringify({
            proposalId: proposalId,
            comment: commentText
        })
    })
        .then(response => {
            if (response.ok) {
                resultDiv.innerHTML = "<span class='success'>Заявка успешно отправлена! Организатор рассмотрит её.</span>";
                document.getElementById('comment').value = '';
            } else {
                response.json().then(data => {
                    resultDiv.innerHTML = `<span class='error'>Ошибка: ${data.message || 'не удалось отправить заявку'}</span>`;
                }).catch(() => {
                    resultDiv.innerHTML = "<span class='error'>Ошибка при отправке заявки. Возможно, места закончились.</span>";
                });
            }
        })
        .catch(error => {
            console.error('Application AJAX Error:', error);
            resultDiv.innerHTML = "<span class='error'>Ошибка сети. Проверьте подключение.</span>";
        });
}

let chatPollingInterval = null;

function initChatRoom(proposalId, receiverId) {
    const messageInput = document.getElementById('chat-message-input');
    const sendButton = document.getElementById('chat-send-btn');

    if (!messageInput || !sendButton) return;

    fetchChatHistory(proposalId, receiverId);

    chatPollingInterval = setInterval(() => {
        fetchChatHistory(proposalId, receiverId);
    }, 3000);

    sendButton.onclick = function() {
        sendChatMessage(proposalId, receiverId);
    };

    messageInput.onkeypress = function(e) {
        if (e.key === 'Enter') {
            sendChatMessage(proposalId, receiverId);
        }
    };
}

function fetchChatHistory(proposalId, receiverId) {
    const chatBox = document.getElementById('chat-messages-container');
    if (!chatBox) return;

    fetch(`/api/chat/history?proposalId=${proposalId}&receiverId=${receiverId}`)
        .then(response => {
            if (response.ok) return response.json();
            throw new Error('Failed to load history');
        })
        .then(messages => {
            const currentUserId = chatBox.getAttribute('data-current-user-id');
            let htmlContent = '';

            messages.forEach(msg => {
                const isSent = msg.sender.id == currentUserId;
                const cssClass = isSent ? 'sent' : 'received';

                htmlContent += `
                <div class="message-item ${cssClass}">
                    <div class="text">${escapeHtml(msg.messageText)}</div>
                    <div style="font-size: 10px; opacity: 0.6; text-align: right; margin-top: 4px;">
                        ${new Date(msg.sentAt).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}
                    </div>
                </div>
            `;
            });

            if (chatBox.innerHTML !== htmlContent) {
                const shouldScroll = chatBox.scrollTop + chatBox.clientHeight >= chatBox.scrollHeight - 50;
                chatBox.innerHTML = htmlContent;
                if (shouldScroll || chatBox.innerHTML === '') {
                    chatBox.scrollTop = chatBox.scrollHeight;
                }
            }
        })
        .catch(err => console.error('History update fail:', err));
}

function sendChatMessage(proposalId, receiverId) {
    const inputEl = document.getElementById('chat-message-input');
    if (!inputEl || !inputEl.value.trim()) return;

    const text = inputEl.value.trim();
    const csrf = getCsrfConfig();

    inputEl.value = '';

    fetch('/api/chat/send', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrf.header]: csrf.token
        },
        body: JSON.stringify({
            proposalId: proposalId,
            receiverId: receiverId,
            messageText: text
        })
    })
        .then(response => {
            if (response.ok) {
                fetchChatHistory(proposalId, receiverId); // Force reload
            } else {
                alert('Не удалось отправить сообщение.');
            }
        })
        .catch(err => console.error('Send message error:', err));
}

function escapeHtml(text) {
    return text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}