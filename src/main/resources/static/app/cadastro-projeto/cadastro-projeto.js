import { checkAuth } from '/utils/auth-helper.js';
checkAuth('ADMIN');

const form = document.getElementById('project-form');
const membersSelect = document.getElementById('members');
const message = document.getElementById('message');

async function carregarUsuarios() {
  const token = localStorage.getItem('token');
  try {
    const response = await fetch('/api/users', {
      headers: { Authorization: `Bearer ${token}` }
    });

    if (!response.ok) throw new Error('Falha ao buscar usuários');

    const users = await response.json();

    users.forEach(user => {
      const option = document.createElement('option');
      option.value = user.id;
      option.textContent = `${user.name} (${user.email})`;
      membersSelect.appendChild(option);
    });
  } catch (err) {
    console.error('Erro ao carregar usuários:', err);
    message.textContent = 'Erro ao carregar membros.';
    message.style.color = 'red';
  }
}

form.addEventListener('submit', async (e) => {
  e.preventDefault();

  const token = localStorage.getItem('token');
  const name = document.getElementById('name').value.trim();
  const description = document.getElementById('description').value.trim();

  const selectedMembers = Array.from(membersSelect.selectedOptions).map(opt => ({ id: opt.value }));

  try {
    const response = await fetch('/api/projects', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({ name, description, members: selectedMembers })
    });

    if (response.ok) {
      message.style.color = 'green';
      message.textContent = 'Projeto criado com sucesso!';
      form.reset();
    } else {
      const error = await response.json();
      message.style.color = 'red';
      message.textContent = error.message || 'Erro ao criar projeto.';
    }
  } catch (err) {
    console.error('Erro:', err);
    message.style.color = 'red';
    message.textContent = 'Erro inesperado. Tente novamente.';
  }
});

carregarUsuarios();
