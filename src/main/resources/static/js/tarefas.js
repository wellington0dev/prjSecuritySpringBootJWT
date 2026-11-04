'use strict'

const API_PROJ = '/proj';
const API_EMP = '/empregado';

let editando = false;
let projetos = [];
let empregados = [];
let idProjetoAtual = null;

// ------- Helpers de autenticação JWT -------
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

function escapeHtml(valor) {
    return String(valor ?? '').replace(/[&<>"']/g, (caractere) => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    })[caractere]);
}

// ------- Bootstrap -------
window.addEventListener('DOMContentLoaded', async () => {
    const ok = await verificarLogin();
    if (!ok) return;

    await carregarProjetos();
    await carregarEmpregados();

    const parametros = new URLSearchParams(window.location.search);
    const idProjeto = parametros.get('idProjeto');

    if (idProjeto && projetos.some(p => String(p.id) === String(idProjeto))) {
        document.getElementById('projeto').value = idProjeto;
    }

    trocarProjeto();
});

async function verificarLogin() {
    try {
        const response = await fetch('/auth/me', {
            headers: authHeaders()
        });

        if (response.status === 401 || !response.ok) {
            logout();
            return false;
        }

        const data = await response.json();
        document.getElementById('userInfo').textContent =
            `Usuário: ${data.username} | Perfis: ${data.roles.join(', ')}`;

        return true;
    } catch (err) {
        console.error(err);
        logout();
        return false;
    }
}

async function carregarProjetos() {
    try {
        const response = await fetch(`${API_PROJ}/listar`, {
            headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        if (response.status === 403) {
            showMessage('Acesso negado: você não tem permissão', 'error');
            return;
        }
        if (!response.ok) {
            showMessage('Erro ao carregar projetos', 'error');
            return;
        }

        projetos = await response.json();

        const select = document.getElementById('projeto');
        select.innerHTML = '<option value="">Selecione...</option>';
        projetos.forEach(proj => {
            select.innerHTML += `<option value="${proj.id}">${escapeHtml(proj.nome)}</option>`;
        });
    } catch (error) {
        console.error('Erro em carregarProjetos:', error);
        showMessage('Erro ao carregar projetos', 'error');
    }
}

async function carregarEmpregados() {
    try {
        const response = await fetch(`${API_EMP}/listar`, {
            headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        if (response.status === 403) {
            showMessage('Acesso negado: você não tem permissão', 'error');
            return;
        }
        if (!response.ok) {
            showMessage('Erro ao carregar empregados', 'error');
            return;
        }

        empregados = await response.json();
        preencherEmpregados();
    } catch (error) {
        console.error('Erro em carregarEmpregados:', error);
        showMessage('Erro ao carregar empregados', 'error');
    }
}

function preencherEmpregados() {
    const select = document.getElementById('empregado');
    select.innerHTML = '<option value="">Selecione...</option>';

    const doProjeto = [];
    const outros = [];

    empregados.forEach(emp => {
        if (idProjetoAtual && String(emp.idProjeto) === String(idProjetoAtual)) {
            doProjeto.push(emp);
        } else {
            outros.push(emp);
        }
    });

    doProjeto.forEach(emp => {
        select.innerHTML += `<option value="${emp.id}">${escapeHtml(emp.nome)} (deste projeto)</option>`;
    });

    outros.forEach(emp => {
        const projeto = emp.nomeProjeto ? ` — ${emp.nomeProjeto}` : ' — sem projeto';
        select.innerHTML += `<option value="${emp.id}">${escapeHtml(emp.nome + projeto)}</option>`;
    });
}

function trocarProjeto() {
    idProjetoAtual = document.getElementById('projeto').value || null;
    limparForm();
    preencherEmpregados();
    atualizarInfoProjeto();
    carregarTarefas();
}

function atualizarInfoProjeto() {
    const info = document.getElementById('infoProjeto');
    const projeto = projetos.find(p => String(p.id) === String(idProjetoAtual));

    if (!projeto) {
        info.textContent = 'Selecione um projeto para gerenciar suas tarefas.';
        return;
    }

    const qtde = projeto.qtdeEmpregados ?? 0;
    info.textContent = `Projeto selecionado: ${projeto.nome} | Empregados associados ao projeto: ${qtde}`;
}

async function carregarTarefas() {
    const tbody = document.querySelector('#tabelaTarefas tbody');
    tbody.innerHTML = '';

    if (!idProjetoAtual) {
        tbody.innerHTML = '<tr><td colspan="5" class="vazio">Selecione um projeto para listar as tarefas.</td></tr>';
        return;
    }

    try {
        const response = await fetch(`${API_PROJ}/${idProjetoAtual}/tarefas/listar`, {
            headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        if (response.status === 403) {
            showMessage('Acesso negado: você não tem permissão', 'error');
            return;
        }
        if (!response.ok) {
            const msg = await response.text();
            showMessage('Erro ao carregar tarefas: ' + msg, 'error');
            return;
        }

        const tarefas = await response.json();
        renderizarTabela(tarefas);
    } catch (error) {
        console.error('Erro em carregarTarefas:', error);
        showMessage('Erro ao carregar tarefas', 'error');
    }
}

function renderizarTabela(tarefas) {
    const tbody = document.querySelector('#tabelaTarefas tbody');
    tbody.innerHTML = '';

    if (!tarefas || tarefas.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="vazio">Nenhuma tarefa cadastrada neste projeto.</td></tr>';
        return;
    }

    tarefas.forEach(tarefa => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${tarefa.numOrdem}</td>
            <td>${escapeHtml(tarefa.descricao)}</td>
            <td>${escapeHtml(tarefa.dataCriacao || '')}</td>
            <td>${escapeHtml(tarefa.nomeEmpregado || '')}</td>
            <td>
                <button class="btn-warning" onclick="editarTarefa(${tarefa.numOrdem})">Editar</button>
                <button class="btn-danger" onclick="excluirTarefa(${tarefa.numOrdem})">Excluir</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

document.getElementById('tarefaForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    if (!idProjetoAtual) {
        showMessage('Selecione um projeto antes de salvar a tarefa.', 'error');
        return;
    }

    const numOrdemOriginal = document.getElementById('numOrdemOriginal').value;
    const numOrdem = Number(document.getElementById('numOrdem').value);
    const descricao = document.getElementById('descricao').value.trim();
    const idEmpregado = document.getElementById('empregado').value;

    if (!Number.isInteger(numOrdem) || numOrdem <= 0) {
        showMessage('O número de ordem deve ser um número inteiro maior que zero.', 'error');
        return;
    }
    if (!descricao) {
        showMessage('Informe a descrição da tarefa.', 'error');
        return;
    }
    if (!idEmpregado) {
        showMessage('Selecione o empregado responsável pela tarefa.', 'error');
        return;
    }

    const tarefa = {
        numOrdem: numOrdem,
        descricao: descricao,
        empregado: { id: Number(idEmpregado) }
    };

    try {
        let response;
        const headers = authHeaders({ 'Content-Type': 'application/json' });

        if (editando) {
            response = await fetch(`${API_PROJ}/${idProjetoAtual}/tarefas/alterar/${numOrdemOriginal}`, {
                method: 'PUT',
                headers: headers,
                body: JSON.stringify(tarefa)
            });
        } else {
            response = await fetch(`${API_PROJ}/${idProjetoAtual}/tarefas/incluir`, {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(tarefa)
            });
        }

        if (response.status === 401) { logout(); return; }
        if (response.status === 403) {
            showMessage('Acesso negado: você não tem permissão', 'error');
            return;
        }

        if (response.ok) {
            showMessage(`Tarefa ${editando ? 'atualizada' : 'criada'} com sucesso!`, 'success');
            limparForm();
            carregarTarefas();
        } else {
            const msg = await response.text();
            showMessage(msg || 'Erro ao salvar a tarefa', 'error');
        }
    } catch (error) {
        console.error('Erro ao salvar tarefa:', error);
        showMessage('Erro ao conectar com o servidor', 'error');
    }
});

async function editarTarefa(numOrdem) {
    if (!idProjetoAtual) return;

    try {
        const response = await fetch(`${API_PROJ}/${idProjetoAtual}/tarefas/listar/${numOrdem}`, {
            headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        if (response.status === 403) {
            showMessage('Acesso negado: você não tem permissão', 'error');
            return;
        }
        if (!response.ok) {
            const msg = await response.text();
            showMessage('Erro ao carregar tarefa: ' + msg, 'error');
            return;
        }

        const tarefa = await response.json();

        document.getElementById('numOrdemOriginal').value = tarefa.numOrdem;
        document.getElementById('numOrdem').value = tarefa.numOrdem;
        document.getElementById('descricao').value = tarefa.descricao;
        document.getElementById('empregado').value = tarefa.idEmpregado || '';

        editando = true;
        document.getElementById('btnSalvar').textContent = 'Atualizar';
        document.getElementById('btnCancelar').classList.remove('hidden');

        document.getElementById('numOrdem').focus();
    } catch (error) {
        console.error('Erro ao carregar tarefa:', error);
        showMessage('Erro ao carregar tarefa', 'error');
    }
}

async function excluirTarefa(numOrdem) {
    if (!idProjetoAtual) return;
    if (!confirm(`Tem certeza que deseja excluir a tarefa de número de ordem ${numOrdem}?`)) return;

    try {
        const response = await fetch(`${API_PROJ}/${idProjetoAtual}/tarefas/remover/${numOrdem}`, {
            method: 'DELETE',
            headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        if (response.status === 403) {
            showMessage('Acesso negado: você não tem permissão', 'error');
            return;
        }

        if (response.ok) {
            showMessage('Tarefa excluída com sucesso!', 'success');
            limparForm();
            carregarTarefas();
        } else {
            const msg = await response.text();
            showMessage(msg || 'Erro ao excluir a tarefa', 'error');
        }
    } catch (error) {
        console.error('Erro ao excluir tarefa:', error);
        showMessage('Erro ao conectar com o servidor', 'error');
    }
}

function limparForm() {
    document.getElementById('tarefaForm').reset();
    document.getElementById('numOrdemOriginal').value = '';
    editando = false;
    document.getElementById('btnSalvar').textContent = 'Salvar';
    document.getElementById('btnCancelar').classList.add('hidden');
}

function irParaDepartamentos() {
    window.location.href = '/departamentos.html';
}

function irParaEmpregados() {
    window.location.href = '/empregados.html';
}

function irParaProjetos() {
    window.location.href = '/projetos.html';
}

function showMessage(message, type) {
    const messageDiv = document.getElementById('message');
    messageDiv.textContent = message;
    messageDiv.className = `message ${type}`;
    messageDiv.style.display = 'block';

    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 5000);
}
