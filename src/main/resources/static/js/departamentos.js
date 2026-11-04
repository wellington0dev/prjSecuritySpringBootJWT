'use strict'

const API_BASE = '/depto';
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

        carregarDepartamentos();
    } catch (err) {
        console.error(err);
        logout();
    }
});

async function carregarDepartamentos() {
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

        const departamentos = await response.json();
        renderizarTabela(departamentos);
    } catch (error) {
        showMessage('Erro ao carregar departamentos', 'error');
        console.error('Erro:', error);
    }
}

function renderizarTabela(departamentos) {
    const tbody = document.querySelector('#tabelaDepartamentos tbody');
    tbody.innerHTML = '';
    departamentos.forEach(depto => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${depto.sigla}</td>
            <td>${depto.nome}</td>
            <td>
                <button class="btn-warning" onclick="editarDepartamento(${depto.id})">Editar</button>
                <button class="btn-danger" onclick="excluirDepartamento(${depto.id})">Excluir</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

document.getElementById('departamentoForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const id = document.getElementById('departamentoId').value;
    const sigla = document.getElementById('sigla').value;
    const nome = document.getElementById('nome').value;
    const departamento = { id, sigla, nome };

    try {
        let response;
        const headers = authHeaders({ 'Content-Type': 'application/json' });
        if (editando) {
            response = await fetch(`${API_BASE}/alterar/${id}`, {
                method: 'PUT', headers, body: JSON.stringify(departamento)
            });
        } else {
            response = await fetch(`${API_BASE}/incluir`, {
                method: 'POST', headers, body: JSON.stringify(departamento)
            });
        }

        if (response.status === 401) { logout(); return; }
        if (response.status === 403) {
            showMessage('Acesso negado: você não tem permissão', 'error');
            return;
        }

        if (response.ok) {
            showMessage(`Departamento ${editando ? 'atualizado' : 'criado'} com sucesso!`, 'success');
            limparForm();
            carregarDepartamentos();
        } else {
            const error = await response.text();
            showMessage('Erro ao salvar departamento: ' + error, 'error');
        }
    } catch (error) {
        showMessage('Erro ao conectar com o servidor', 'error');
        console.error('Erro:', error);
    }
});

async function editarDepartamento(id) {
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
            const departamento = await response.json();
            document.getElementById('departamentoId').value = departamento.id;
            document.getElementById('sigla').value = departamento.sigla;
            document.getElementById('nome').value = departamento.nome;
            editando = true;
            document.getElementById('btnSalvar').textContent = 'Atualizar';
            document.getElementById('btnCancelar').classList.remove('hidden');
        }
    } catch (error) {
        showMessage('Erro ao carregar departamento', 'error');
    }
}

async function excluirDepartamento(id) {
    if (!confirm('Tem certeza que deseja excluir este departamento?')) return;

    try {
        const response = await fetch(`${API_BASE}/remover/${id}`, {
            method: 'DELETE',
            headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        if (response.ok) {
            showMessage('Departamento excluído com sucesso!', 'success');
            carregarDepartamentos();
        } else {
            showMessage('Erro ao excluir departamento', 'error');
        }
    } catch (error) {
        showMessage('Erro ao conectar com o servidor', 'error');
    }
}

function limparForm() {
    document.getElementById('departamentoForm').reset();
    document.getElementById('departamentoId').value = '';
    editando = false;
    document.getElementById('btnSalvar').textContent = 'Salvar';
    document.getElementById('btnCancelar').classList.add('hidden');
}

function irParaEmpregados() {
    window.location.href = '/empregados.html';
}

function irParaProjetos() {
    window.location.href = '/projetos.html';
}

function irParaTarefas() {
    window.location.href = '/tarefas.html';
}

function showMessage(message, type) {
    const messageDiv = document.getElementById('message');
    messageDiv.textContent = message;
    messageDiv.className = `message ${type}`;
    messageDiv.style.display = 'block';
    setTimeout(() => { messageDiv.style.display = 'none'; }, 5000);
}