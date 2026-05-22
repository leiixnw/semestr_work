async function applyForTrip(proposalId) {
    try {
        const response = await fetch('/api/applications/apply', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ proposalId: proposalId, comment: '' })
        });

        if (!response.ok) {
            alert('Ошибка при подаче заявки');
            return;
        }

        alert('Заявка отправлена!');
    } catch (error) {
        console.error('Error:', error);
    }
}

document.querySelectorAll('form').forEach(form => {
    form.addEventListener('submit', function() {
        const submitBtn = this.querySelector('button[type="submit"]');
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.textContent = 'Отправляется...';
        }
    });
});