'use strict'

const API_BASE = '/empregado';
const API_DEPTO = '/depto';
const API_PROJ = '/proj';

let editando = false;
let departamentos = [];
let projetos = [];

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

// ------- Bootstrap -------
window.addEventListener('DOMContentLoaded', async () => {
    const ok = await verificarLogin();
    if (!ok) return;

    // Carrega os selects primeiro para garantir que as listas estejam disponíveis ao montar a tabela
    await carregarDepartamentosSelect();
    await carregarProjetosSelect();
    await carregarEmpregados();
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

async function carregarProjetosSelect() {
    try {
        const response = await fetch(`${API_PROJ}/listar`, {
            method: 'GET',
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

        // CORREÇÃO: Lê o JSON apenas UMA vez e guarda no array global
        projetos = await response.json();

        const select = document.getElementById('projeto');
        if (!select) return;

        select.innerHTML = '<option value="">Nenhum</option>';

        projetos.forEach(proj => {
            select.innerHTML += `<option value="${proj.id}">${proj.nome}</option>`;
        });

    } catch (error) {
        console.error('Erro em carregarProjetosSelect:', error);
        showMessage('Erro ao carregar projetos', 'error');
    }
}

async function carregarDepartamentosSelect() {
    try {
        const response = await fetch(`${API_DEPTO}/listar`, {
            headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        if (response.status === 403) {
            showMessage('Acesso negado: você não tem permissão', 'error');
            return;
        }

        departamentos = await response.json();

        const select = document.getElementById('departamento');
        select.innerHTML = '<option value="">Selecione...</option>';

        // CORREÇÃO: Removido o caractere '-' de sintaxe inválida
        departamentos.forEach(dep => {
            select.innerHTML += `<option value="${dep.id}">${dep.sigla} - ${dep.nome}</option>`;
        });

    } catch (error) {
        console.error('Erro em carregarDepartamentosSelect:', error);
        showMessage('Erro ao carregar departamentos', 'error');
    }
}

async function carregarEmpregados() {
    try {
        const response = await fetch(`${API_BASE}/listar`, {
            headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        if (response.status === 403) {
            showMessage('Acesso negado: você não tem permissão', 'error');
            return;
        }

        const empregados = await response.json();
        renderizarTabela(empregados);

    } catch (error) {
        console.error('Erro em carregarEmpregados:', error);
        showMessage('Erro ao carregar empregados', 'error');
    }
}

function renderizarTabela(empregados) {
    const tbody = document.querySelector('#tabelaEmpregados tbody');
    tbody.innerHTML = '';

    empregados.forEach(emp => {
        let nomeProjeto = 'Nenhum';

        // Mapeia o nome do projeto se vier como string, objeto aninhado ou ID
        if (emp.nomeProjeto) {
            nomeProjeto = emp.nomeProjeto;
        } else if (emp.projeto && emp.projeto.nome) {
            nomeProjeto = emp.projeto.nome;
        } else if (emp.idProjeto) {
            const proj = projetos.find(p => p.id == emp.idProjeto);
            if (proj) nomeProjeto = proj.nome;
        }

        const tr = document.createElement('tr');

        tr.innerHTML = `
            <td>${emp.cpf}</td>
            <td>${emp.nome}</td>
            <td>${emp.nomeDepartamento || (emp.departamento ? emp.departamento.nome : '')}</td>
            <td>${nomeProjeto}</td>
            <td>
                <button class="btn-warning" onclick="editarEmpregado(${emp.id})">Editar</button>
                <button class="btn-danger" onclick="excluirEmpregado(${emp.id})">Excluir</button>
            </td>
        `;

        tbody.appendChild(tr);
    });
}

document.getElementById('empregadoForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const idEmpregado = document.getElementById('idEmpregado').value;
    const cpf = document.getElementById('cpf').value;
    const nome = document.getElementById('nome').value;
    const idDepartamento = document.getElementById('departamento').value;
    const idProjeto = document.getElementById('projeto').value;

    const departamento = departamentos.find(d => d.id == idDepartamento) || null;
    const projeto = projetos.find(p => p.id == idProjeto) || null; // CORREÇÃO: Garante null se não selecionado

    const empregado = {
        idEmpregado: idEmpregado || null,
        cpf: cpf,
        nome: nome,
        departamento: idDepartamento ? { id: Number(idDepartamento) } : null,
        projeto: idProjeto ? { id: Number(idProjeto) } : null
    };

    try {
        let response;
        const headers = authHeaders({ 'Content-Type': 'application/json' });

        if (editando) {
            response = await fetch(`${API_BASE}/alterar/${idEmpregado}`, {
                method: 'PUT',
                headers: headers,
                body: JSON.stringify(empregado)
            });
        } else {
            response = await fetch(`${API_BASE}/incluir`, {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(empregado)
            });
        }

        if (response.status === 401) { logout(); return; }
        if (response.status === 403) {
            showMessage('Acesso negado: você não tem permissão', 'error');
            return;
        }

        if (response.ok) {
            showMessage(`Empregado ${editando ? 'atualizado' : 'criado'} com sucesso!`, 'success');
            limparForm();
            carregarEmpregados();
        } else {
            const msg = await response.text();
            showMessage(msg, 'error');
        }

    } catch (error) {
        showMessage('Erro ao salvar empregado', 'error');
    }
});

async function editarEmpregado(id) {
    try {
        const response = await fetch(`${API_BASE}/listar/${id}`, {
            headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        if (response.status === 403) {
            showMessage('Acesso negado: você não tem permissão', 'error');
            return;
        }
        if (!response.ok) {
            showMessage('Erro ao carregar empregado', 'error');
            return;
        }

        const emp = await response.json();

        document.getElementById('idEmpregado').value = id;
        document.getElementById('cpf').value = emp.cpf;
        document.getElementById('nome').value = emp.nome;
        
        // Atribui Departamento
        const idDep = emp.idDepartamento || (emp.departamento ? emp.departamento.id : '');
        document.getElementById('departamento').value = idDep;

        // CORREÇÃO: Atribui Projeto ao formulário de edição
        const idProj = emp.idProjeto || (emp.projeto ? emp.projeto.id : '');
        document.getElementById('projeto').value = idProj;

        editando = true;

        document.getElementById('btnSalvar').textContent = 'Atualizar';
        document.getElementById('btnCancelar').classList.remove('hidden');

    } catch (error) {
        showMessage('Erro ao carregar empregado', 'error');
    }
}

async function excluirEmpregado(id) {
    if (!confirm('Deseja excluir este empregado?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/remover/${id}`, {
            method: 'DELETE',
            headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        if (response.status === 403) {
            showMessage('Acesso negado: você não tem permissão', 'error');
            return;
        }

        if (response.ok) {
            showMessage('Empregado excluído com sucesso!', 'success');
            carregarEmpregados();
        } else {
            showMessage('Erro ao excluir empregado', 'error');
        }

    } catch (error) {
        showMessage('Erro ao excluir empregado', 'error');
    }
}

function limparForm() {
    document.getElementById('empregadoForm').reset();
    document.getElementById('idEmpregado').value = '';
    document.getElementById('projeto').value = '';
    document.getElementById('departamento').value = '';
    editando = false;

    document.getElementById('btnSalvar').textContent = 'Salvar';
    document.getElementById('btnCancelar').classList.add('hidden');
}

function irParaDepartamentos() {
    window.location.href = '/departamentos.html';
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

    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 5000);
}