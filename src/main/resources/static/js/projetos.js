'use strict'

const API_BASE = '/proj';
let editando = false;

// ------- Helpers de autenticação -------
function getToken() {
    return localStorage.getItem('jwt');
}

function authHeaders(extra = {}) {
    const token = getToken();
    return token
        ? { ...extra, 'Authorization': 'Bearer ' + token }
        : extra;
}

function logout() {
    localStorage.removeItem('jwt');
    localStorage.removeItem('username');
    localStorage.removeItem('roles');
    window.location.href = '/index.html';
}

// ------- Bootstrap -------
window.addEventListener('DOMContentLoaded', async () => {
    const token = getToken();
    if (!token) {
        window.location.href = 'index.html';
        return;
    }

    try {
        const response = await fetch('/auth/me', {
            headers: authHeaders()
        });

        if (response.status === 401) {
            logout();
            return;
        }

        const data = await response.json();
        document.getElementById('userInfo').textContent =
            `Usuário: ${data.username} | Perfis: ${data.roles.join(', ')}`;

        carregarProjetos();
    } catch (err) {
        console.error(err);
        logout();
    }
});

async function carregarProjetos() {
    try {
        const response = await fetch(`${API_BASE}/listar`, {
            method: 'GET',
            headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        if (response.status === 403) {
            showMessage('Acesso negado: você não tem permissão', 'error');
            return;
        }

        const projetos = await response.json();
        renderizarTabela(projetos);
    } catch (error) {
        showMessage('Erro ao carregar os projetos', 'error');
        console.error('Erro:', error);
    }
}

function renderizarTabela(projetos) {
    const tbody = document.querySelector('#tabelaProjetos tbody');
    tbody.innerHTML = '';
    projetos.forEach(proj => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${proj.nome}</td>
            <td>${proj.dataCriacao}</td>
            <td>
                <button class="btn-primary" onclick="verTarefasProjeto(${proj.id})">Tarefas</button>
                <button class="btn-warning" onclick="editarProjeto(${proj.id})">Editar</button>
                <button class="btn-danger" onclick="excluirProjeto(${proj.id})">Excluir</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

document.getElementById('projetoForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const id = document.getElementById('projetoId').value;
    const nome = document.getElementById('nome').value;
	const dataCriacao = null;
    const projeto = { id, nome };

    try {
        let response;
        const headers = authHeaders({ 'Content-Type': 'application/json' });
        if (editando) {
            response = await fetch(`${API_BASE}/alterar/${id}`, {
                method: 'PUT', headers, body: JSON.stringify(projeto)
            });
        } else {
            response = await fetch(`${API_BASE}/incluir`, {
                method: 'POST', headers, body: JSON.stringify(projeto)
            });
        }

        if (response.status === 401) { logout(); return; }
        if (response.status === 403) {
            showMessage('Acesso negado: você não tem permissão', 'error');
            return;
        }

        if (response.ok) {
            showMessage(`Projeto ${editando ? 'atualizado' : 'criado'} com sucesso!`, 'success');
            limparForm();
            carregarProjetos();
        } else {
            const error = await response.text();
            showMessage('Erro ao salvar o projeto: ' + error, 'error');
        }
    } catch (error) {
        showMessage('Erro ao conectar com o servidor', 'error');
        console.error('Erro:', error);
    }
});

async function editarProjeto(id) {
    try {
        const response = await fetch(`${API_BASE}/listar/${id}`, {
            method: 'GET',
            headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        if (response.status === 403) {
            showMessage('Acesso negado: você não tem permissão', 'error');
            return;
        }
        if (response.ok) {
            const projeto = await response.json();
            document.getElementById('projetoId').value = projeto.id;
            document.getElementById('nome').value = projeto.nome;
            editando = true;
            document.getElementById('btnSalvar').textContent = 'Atualizar';
            document.getElementById('btnCancelar').classList.remove('hidden');
        }
    } catch (error) {
        showMessage('Erro ao carregar o projeto', 'error');
    }
}

async function excluirProjeto(id) {
    if (!confirm('Tem certeza que deseja excluir este projeto?')) return;

    try {
        const response = await fetch(`${API_BASE}/remover/${id}`, {
            method: 'DELETE',
            headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        if (response.ok) {
            showMessage('Projeto excluído com sucesso!', 'success');
            carregarProjetos();
        } else {
            showMessage('Erro ao excluir o projeto', 'error');
        }
    } catch (error) {
        showMessage('Erro ao conectar com o servidor', 'error');
    }
}

function limparForm() {
    document.getElementById('projetoForm').reset();
    document.getElementById('projetoId').value = '';
    editando = false;
    document.getElementById('btnSalvar').textContent = 'Salvar';
    document.getElementById('btnCancelar').classList.add('hidden');
}

function irParaEmpregados() {
    window.location.href = '/empregados.html';
}

function irParaDepartamentos() {
    window.location.href = '/departamentos.html';
}

function irParaTarefas() {
    window.location.href = '/tarefas.html';
}

function verTarefasProjeto(id) {
    window.location.href = `/tarefas.html?idProjeto=${id}`;
}

function showMessage(message, type) {
    const messageDiv = document.getElementById('message');
    messageDiv.textContent = message;
    messageDiv.className = `message ${type}`;
    messageDiv.style.display = 'block';
    setTimeout(() => { messageDiv.style.display = 'none'; }, 5000);
}