/**
 * AutoGestão — Front-end Single Page Application (SPA)
 * Comunicação direta com a API RESTful de Veículos
 */

// 1. Configuração da API Base
const API_BASE = window.location.origin.includes(':8080')
  ? '/veiculos'
  : 'http://localhost:8080/veiculos';

// 2. Estado Global da Aplicação
const appState = {
  veiculos: [],
  estatisticas: null,
  veiculoSelecionadoParaExcluir: null,
  filtroSemanaAtivo: false,
};

// 3. Inicialização ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
  inicializarEventos();
  carregarDadosCompletos();
});

function inicializarEventos() {
  // Modal de Cadastro / Edição
  document.getElementById('btnNovoVeiculo').addEventListener('click', abrirModalParaCriacao);
  document.getElementById('btnFecharModal').addEventListener('click', fecharModalVeiculo);
  document.getElementById('btnCancelarModal').addEventListener('click', fecharModalVeiculo);
  document.getElementById('veiculoForm').addEventListener('submit', lidarComSubmitVeiculo);

  // Modal de Exclusão
  document.getElementById('btnCancelarDelete').addEventListener('click', fecharModalDelete);
  document.getElementById('btnConfirmarDelete').addEventListener('click', confirmarExclusao);

  // Filtros
  document.getElementById('filterForm').addEventListener('submit', lidarComFiltro);
  document.getElementById('btnLimparFiltros').addEventListener('click', limparFiltros);
  document.getElementById('btnClearFilterTag').addEventListener('click', limparFiltros);
  document.getElementById('btnVerSemana').addEventListener('click', filtrarApenasUltimaSemana);

  // Fechar modais ao clicar no backdrop
  document.getElementById('veiculoModal').addEventListener('click', (e) => {
    if (e.target.id === 'veiculoModal') fecharModalVeiculo();
  });
  document.getElementById('deleteModal').addEventListener('click', (e) => {
    if (e.target.id === 'deleteModal') fecharModalDelete();
  });

  // Atualizar ícones do Lucide
  lucide.createIcons();
}

// 4. Carregamento de Dados da API
async function carregarDadosCompletos() {
  await Promise.all([
    carregarVeiculos(),
    carregarEstatisticas(),
  ]);
  atualizarStatusApi(true);
}

async function carregarVeiculos(filtros = {}) {
  try {
    const params = new URLSearchParams();
    if (filtros.marca) params.append('marca', filtros.marca);
    if (filtros.ano) params.append('ano', filtros.ano);
    if (filtros.cor) params.append('cor', filtros.cor);

    const url = params.toString() ? `${API_BASE}?${params.toString()}` : API_BASE;
    const response = await fetch(url);

    if (!response.ok) throw new Error('Falha ao carregar catálogo de veículos');

    const lista = await response.json();
    appState.veiculos = lista;
    renderizarTabela(lista);
    atualizarKpisGerais();
  } catch (erro) {
    console.error(erro);
    mostrarToast('Não foi possível conectar à API. Verifique se o Back-end está rodando.', 'erro');
    atualizarStatusApi(false);
  }
}

async function carregarEstatisticas() {
  try {
    const response = await fetch(`${API_BASE}/relatorios`);
    if (!response.ok) throw new Error('Falha ao carregar estatísticas');

    const stats = await response.json();
    appState.estatisticas = stats;
    renderizarEstatisticas(stats);
  } catch (erro) {
    console.error('Erro ao buscar relatórios:', erro);
  }
}

async function filtrarApenasUltimaSemana() {
  try {
    const response = await fetch(`${API_BASE}/semana`);
    if (!response.ok) throw new Error('Falha ao buscar carros da semana');

    const carrosSemana = await response.json();
    appState.filtroSemanaAtivo = true;
    appState.veiculos = carrosSemana;

    document.getElementById('filterTag').classList.remove('hidden');
    document.getElementById('filterTag').querySelector('span').innerText = 'Registrados na Última Semana';

    renderizarTabela(carrosSemana);
    mostrarToast(`Exibindo ${carrosSemana.length} veículo(s) cadastrados nos últimos 7 dias.`);
  } catch (erro) {
    console.error(erro);
    mostrarToast('Erro ao listar veículos da última semana', 'erro');
  }
}

// 5. Renderização dos Elementos na Tela
function renderizarTabela(veiculos) {
  const tbody = document.getElementById('tabelaVeiculosCorpo');
  const contador = document.getElementById('contadorExibidos');
  contador.innerText = veiculos.length;

  if (veiculos.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="8" class="text-center py-12">
          <div class="flex flex-col items-center justify-center text-slate-400 gap-2">
            <i data-lucide="package-open" class="w-10 h-10 stroke-[1.5]"></i>
            <p class="font-medium text-slate-600 text-sm">Nenhum veículo encontrado</p>
            <p class="text-xs text-slate-400">Tente ajustar seus filtros ou cadastre um novo veículo.</p>
          </div>
        </td>
      </tr>
    `;
    lucide.createIcons();
    return;
  }

  tbody.innerHTML = veiculos.map(v => {
    const dataCriacao = v.created ? formatarData(v.created) : '—';
    const vendidoClass = v.vendido
      ? 'bg-slate-100 text-slate-600 hover:bg-slate-200'
      : 'bg-emerald-50 text-emerald-700 hover:bg-emerald-100 border border-emerald-200';
    const vendidoTexto = v.vendido ? 'Vendido' : 'À Venda';

    return `
      <tr class="hover:bg-slate-50/80 transition-colors group">
        <td class="px-6 py-4 font-mono text-xs text-slate-400">#${v.id}</td>
        <td class="px-6 py-4">
          <div class="font-semibold text-slate-900">${v.veiculo}</div>
          ${v.descricao ? `<p class="text-xs text-slate-400 truncate max-w-xs mt-0.5" title="${v.descricao}">${v.descricao}</p>` : ''}
        </td>
        <td class="px-6 py-4">
          <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold ${obterEstiloMarca(v.marca)}">
            ${v.marca}
          </span>
        </td>
        <td class="px-6 py-4 font-medium text-slate-700">${v.ano}</td>
        <td class="px-6 py-4 text-slate-600">${v.cor || '<span class="text-slate-300">Não inf.</span>'}</td>
        <td class="px-6 py-4">
          <button onclick="alternarStatusVendido(${v.id}, ${v.vendido})" title="Clique para alternar status via PATCH" class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold transition-all cursor-pointer ${vendidoClass}">
            <span class="w-1.5 h-1.5 rounded-full ${v.vendido ? 'bg-slate-400' : 'bg-emerald-500'}"></span>
            ${vendidoTexto}
          </button>
        </td>
        <td class="px-6 py-4 text-xs text-slate-500">${dataCriacao}</td>
        <td class="px-6 py-4 text-right">
          <div class="flex items-center justify-end gap-2 opacity-90 group-hover:opacity-100">
            <button onclick="abrirModalParaEdicao(${v.id})" title="Editar Veículo (PUT)" class="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
              <i data-lucide="edit-3" class="w-4 h-4"></i>
            </button>
            <button onclick="solicitarExclusao(${v.id}, '${v.veiculo.replace(/'/g, "\\'")}')" title="Excluir Veículo (DELETE)" class="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors">
              <i data-lucide="trash-2" class="w-4 h-4"></i>
            </button>
          </div>
        </td>
      </tr>
    `;
  }).join('');

  lucide.createIcons();
}

function renderizarEstatisticas(stats) {
  if (!stats) return;

  // KPIs
  document.getElementById('kpiNaoVendidos').innerText = stats.totalNaoVendidos || 0;
  document.getElementById('kpiSemana').innerText = stats.totalUltimaSemana || 0;

  // Décadas
  const containerDecadas = document.getElementById('containerDecadas');
  const decadas = stats.distribuicaoPorDecada || {};
  const chavesDecadas = Object.keys(decadas).sort();

  if (chavesDecadas.length === 0) {
    containerDecadas.innerHTML = '<p class="text-xs text-slate-400 text-center py-4">Nenhum registro para calcular décadas.</p>';
  } else {
    const totalGeral = Object.values(decadas).reduce((a, b) => a + b, 0) || 1;
    containerDecadas.innerHTML = chavesDecadas.map(decada => {
      const qtd = decadas[decada];
      const pct = Math.round((qtd / totalGeral) * 100);
      return `
        <div>
          <div class="flex justify-between text-xs font-semibold mb-1">
            <span class="text-slate-700">${decada}</span>
            <span class="text-slate-500">${qtd} veículo(s) <span class="text-slate-400">(${pct}%)</span></span>
          </div>
          <div class="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
            <div class="bg-blue-600 h-2 rounded-full transition-all duration-500" style="width: ${pct}%"></div>
          </div>
        </div>
      `;
    }).join('');
  }

  // Fabricantes
  const containerFabricantes = document.getElementById('containerFabricantes');
  const fabricantes = stats.distribuicaoPorFabricante || {};
  const chavesMarcas = Object.keys(fabricantes).sort((a, b) => fabricantes[b] - fabricantes[a]);

  if (chavesMarcas.length === 0) {
    containerFabricantes.innerHTML = '<p class="text-xs text-slate-400 text-center py-4">Nenhum registro para calcular fabricantes.</p>';
  } else {
    containerFabricantes.innerHTML = `
      <div class="grid grid-cols-2 sm:grid-cols-3 gap-2.5">
        ${chavesMarcas.map(marca => `
          <div class="bg-slate-50 p-2.5 rounded-xl border border-slate-200/60 flex items-center justify-between">
            <span class="text-xs font-bold text-slate-800 truncate">${marca}</span>
            <span class="text-xs font-extrabold px-2 py-0.5 rounded-md bg-white border border-slate-200 text-slate-700 shadow-2xs">${fabricantes[marca]}</span>
          </div>
        `).join('')}
      </div>
    `;
  }
}

function atualizarKpisGerais() {
  const total = appState.veiculos.length;
  const vendidos = appState.veiculos.filter(v => v.vendido).length;
  document.getElementById('kpiTotalGeral').innerText = total;
  document.getElementById('kpiVendidos').innerText = vendidos;
}

// 6. Ações CRUD (POST, PUT, PATCH, DELETE)
function abrirModalParaCriacao() {
  document.getElementById('modalTitulo').innerText = 'Novo Veículo';
  document.getElementById('veiculoForm').reset();
  document.getElementById('veiculoId').value = '';
  document.getElementById('campoVendido').checked = false;
  document.getElementById('veiculoModal').classList.remove('hidden');
}

async function abrirModalParaEdicao(id) {
  try {
    const response = await fetch(`${API_BASE}/${id}`);
    if (!response.ok) throw new Error('Veículo não encontrado');

    const v = await response.json();
    document.getElementById('modalTitulo').innerText = `Editar Veículo #${v.id}`;
    document.getElementById('veiculoId').value = v.id;
    document.getElementById('campoVeiculo').value = v.veiculo;
    document.getElementById('campoMarca').value = v.marca;
    document.getElementById('campoAno').value = v.ano;
    document.getElementById('campoCor').value = v.cor || '';
    document.getElementById('campoDescricao').value = v.descricao || '';
    document.getElementById('campoVendido').checked = v.vendido;

    document.getElementById('veiculoModal').classList.remove('hidden');
  } catch (erro) {
    console.error(erro);
    mostrarToast('Erro ao carregar dados do veículo para edição', 'erro');
  }
}

function fecharModalVeiculo() {
  document.getElementById('veiculoModal').classList.add('hidden');
}

async function lidarComSubmitVeiculo(e) {
  e.preventDefault();

  const id = document.getElementById('veiculoId').value;
  const payload = {
    veiculo: document.getElementById('campoVeiculo').value.trim(),
    marca: document.getElementById('campoMarca').value,
    ano: parseInt(document.getElementById('campoAno').value, 10),
    cor: document.getElementById('campoCor').value.trim() || null,
    descricao: document.getElementById('campoDescricao').value.trim() || null,
    vendido: document.getElementById('campoVendido').checked,
  };

  const isEdit = Boolean(id);
  const url = isEdit ? `${API_BASE}/${id}` : API_BASE;
  const metodo = isEdit ? 'PUT' : 'POST';

  try {
    const response = await fetch(url, {
      method: metodo,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      const erroBody = await response.json().catch(() => ({}));
      throw new Error(erroBody.mensagem || 'Falha ao salvar veículo');
    }

    fecharModalVeiculo();
    mostrarToast(isEdit ? 'Veículo atualizado com sucesso!' : 'Veículo cadastrado com sucesso!');
    await carregarDadosCompletos();
  } catch (erro) {
    console.error(erro);
    mostrarToast(erro.message, 'erro');
  }
}

// Demonstração do PATCH (Atualização Parcial)
window.alternarStatusVendido = async function(id, statusAtual) {
  try {
    const response = await fetch(`${API_BASE}/${id}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ vendido: !statusAtual }),
    });

    if (!response.ok) throw new Error('Falha ao atualizar status do veículo');

    mostrarToast(`Status alterado para "${!statusAtual ? 'Vendido' : 'À Venda'}" via PATCH`);
    await carregarDadosCompletos();
  } catch (erro) {
    console.error(erro);
    mostrarToast(erro.message, 'erro');
  }
};

// Exclusão (DELETE)
window.solicitarExclusao = function(id, nome) {
  appState.veiculoSelecionadoParaExcluir = id;
  document.getElementById('deleteNomeVeiculo').innerText = `"${nome}" (#${id})`;
  document.getElementById('deleteModal').classList.remove('hidden');
};

function fecharModalDelete() {
  document.getElementById('deleteModal').classList.add('hidden');
  appState.veiculoSelecionadoParaExcluir = null;
}

async function confirmarExclusao() {
  const id = appState.veiculoSelecionadoParaExcluir;
  if (!id) return;

  try {
    const response = await fetch(`${API_BASE}/${id}`, {
      method: 'DELETE',
    });

    if (!response.ok && response.status !== 204) {
      throw new Error('Falha ao excluir veículo');
    }

    fecharModalDelete();
    mostrarToast('Veículo excluído com sucesso!');
    await carregarDadosCompletos();
  } catch (erro) {
    console.error(erro);
    mostrarToast(erro.message, 'erro');
  }
}

// 7. Filtros
function lidarComFiltro(e) {
  e.preventDefault();
  const marca = document.getElementById('filterMarca').value;
  const ano = document.getElementById('filterAno').value;
  const cor = document.getElementById('filterCor').value.trim();

  const temFiltro = Boolean(marca || ano || cor);
  const tag = document.getElementById('filterTag');

  if (temFiltro) {
    tag.classList.remove('hidden');
    tag.querySelector('span').innerText = `Filtros: ${[marca, ano, cor].filter(Boolean).join(' | ')}`;
  } else {
    tag.classList.add('hidden');
  }

  carregarVeiculos({ marca, ano, cor });
}

function limparFiltros() {
  document.getElementById('filterForm').reset();
  document.getElementById('filterTag').classList.add('hidden');
  appState.filtroSemanaAtivo = false;
  carregarVeiculos();
}

// 8. Funções Utilitárias
function formatarData(isoString) {
  try {
    const data = new Date(isoString);
    return data.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' });
  } catch {
    return isoString;
  }
}

function obterEstiloMarca(marca) {
  const map = {
    'Honda': 'bg-red-50 text-red-700 border border-red-200',
    'Toyota': 'bg-rose-50 text-rose-700 border border-rose-200',
    'Volkswagen': 'bg-blue-50 text-blue-700 border border-blue-200',
    'Ford': 'bg-sky-50 text-sky-700 border border-sky-200',
    'Fiat': 'bg-amber-50 text-amber-700 border border-amber-200',
    'Chevrolet': 'bg-yellow-50 text-yellow-800 border border-yellow-200',
    'Jeep': 'bg-emerald-50 text-emerald-800 border border-emerald-200',
    'Hyundai': 'bg-indigo-50 text-indigo-700 border border-indigo-200',
    'Renault': 'bg-orange-50 text-orange-700 border border-orange-200',
    'Nissan': 'bg-purple-50 text-purple-700 border border-purple-200',
  };
  return map[marca] || 'bg-slate-100 text-slate-700 border border-slate-200';
}

function atualizarStatusApi(online) {
  const badge = document.getElementById('apiStatusBadge');
  if (online) {
    badge.className = 'hidden sm:flex items-center gap-2 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200';
    badge.innerHTML = '<span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span> API Online';
  } else {
    badge.className = 'hidden sm:flex items-center gap-2 px-3 py-1 rounded-full text-xs font-semibold bg-rose-50 text-rose-700 border border-rose-200';
    badge.innerHTML = '<span class="w-2 h-2 rounded-full bg-rose-500"></span> API Offline';
  }
}

function mostrarToast(mensagem, tipo = 'sucesso') {
  const container = document.getElementById('toastContainer');
  const toast = document.createElement('div');

  const cores = tipo === 'erro'
    ? 'bg-rose-600 text-white'
    : 'bg-slate-900 text-white';

  const icone = tipo === 'erro' ? 'alert-triangle' : 'check';

  toast.className = `flex items-center gap-3 px-4 py-3 rounded-xl shadow-xl text-sm font-medium ${cores} animate-slide-in pointer-events-auto max-w-sm`;
  toast.innerHTML = `
    <i data-lucide="${icone}" class="w-4 h-4 shrink-0"></i>
    <span class="flex-1">${mensagem}</span>
  `;

  container.appendChild(toast);
  lucide.createIcons({ root: toast });

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(-10px)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}
