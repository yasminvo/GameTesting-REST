import { checkAuth } from '/utils/auth-helper.js';

checkAuth('ADMIN');

document.getElementById('logout-btn').addEventListener('click', () => {
  localStorage.removeItem('token');
  window.location.href = '/app/login/index.html';
});

const projectList = document.getElementById("project-list");
const sortSelect = document.getElementById("sort-select");

async function carregarProjetos(sort = "creationDate") {
  try {
    const token = localStorage.getItem("token");
    const response = await fetch(`/api/projects?sort=${sort}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

    if (!response.ok) {
      throw new Error("Erro ao carregar projetos");
    }

    const projetos = await response.json();
    renderizarProjetos(projetos);
  } catch (error) {
    console.error("Erro ao buscar projetos:", error);
    projectList.innerHTML = "<li>Erro ao carregar projetos.</li>";
  }
}

function renderizarProjetos(projetos) {
  projectList.innerHTML = "";

  if (projetos.length === 0) {
    projectList.innerHTML = "<li>Nenhum projeto cadastrado.</li>";
    return;
  }

  projetos.forEach((projeto) => {
    const li = document.createElement("li");
    li.innerHTML = `
      <strong>${projeto.name}</strong> - ${projeto.description} <br>
      <small>Criado em: ${new Date(projeto.creationDate).toLocaleString()}</small>
    `;
    projectList.appendChild(li);
  });
}

sortSelect.addEventListener("change", () => {
  carregarProjetos(sortSelect.value);
});

carregarProjetos();