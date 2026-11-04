'use strict'

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('loginForm').addEventListener('submit', async (e) => {
        e.preventDefault();

        const conta = document.getElementById('conta').value;
        const senha = document.getElementById('senha').value;

        try {
            const response = await fetch('/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ conta, senha })
            });

            const data = await response.json();

            if (response.ok) {
                // Armazena o token para uso posterior
                localStorage.setItem('jwt', data.meuJwt);
                //localStorage.setItem('username', data.username);
                //localStorage.setItem('roles', JSON.stringify(data.roles));

                showMessage('Login realizado com sucesso', 'success');
                setTimeout(() => {
                    window.location.href = 'departamentos.html';
                }, 800);
            } else {
                showMessage(data.erro || 'Falha no login', 'error');
            }
        } catch (err) {
            console.error(err);
            showMessage('Erro de conexão', 'error');
        }
    });
});

function showMessage(message, type) {
    const messageDiv = document.getElementById('message');
    messageDiv.textContent = message;
    messageDiv.className = type;
    messageDiv.style.display = 'block';
    setTimeout(() => { messageDiv.style.display = 'none'; }, 3000);
}