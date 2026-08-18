const state = {
    places: [],
    activePlaceId: null,
    modalType: null
};

const placeForm = document.getElementById('placeForm');
const photoForm = document.getElementById('photoForm');
const placeSelect = document.getElementById('placeSelect');
const modalBackdrop = document.getElementById('modalBackdrop');
const modalTitle = document.getElementById('modalTitle');
const closeModalButton = document.getElementById('closeModal');
// form inputs for visit date handled as month (yyyy-MM) on client side
// map-related elements removed from UI

function init() {
    bindEvents();
    loadPlaces();
}

function bindEvents() {
    placeForm.addEventListener('submit', handlePlaceSubmit);
    photoForm.addEventListener('submit', handlePhotoSubmit);
    // location picker removed

    document.querySelectorAll('[data-open-modal]').forEach((button) => {
        button.addEventListener('click', () => openModal(button.dataset.openModal));
    });

    closeModalButton.addEventListener('click', closeModal);
    modalBackdrop.addEventListener('click', (event) => {
        if (event.target === modalBackdrop) {
            closeModal();
        }
    });
}

async function loadPlaces() {
    try {
        const response = await fetch('/api/places');
        if (!response.ok) {
            throw new Error('No se pudieron cargar los lugares');
        }
        state.places = await response.json();
        renderCityList();
        populatePlaceSelect();
        if (state.places.length) {
            renderCityList();
        } else {
            renderEmptyState();
        }
    } catch (error) {
        renderEmptyState(error.message);
    }
}
// ---- City list & photo carousel ----
function renderCityList() {
    const list = document.getElementById('cityList');
    list.innerHTML = '';

    state.places.forEach((place) => {
        const card = document.createElement('button');
        card.className = 'city-card';
        card.type = 'button';

        const coverUrl = place.coverImageUrl || (place.photos && place.photos[0] && place.photos[0].imageUrl) || '';
        const cover = coverUrl
            ? document.createElement('img')
            : document.createElement('div');

        if (coverUrl) {
            cover.className = 'city-cover';
            cover.src = coverUrl;
            cover.alt = place.name;
        } else {
            cover.className = 'city-cover city-cover-placeholder';
            cover.textContent = place.name;
        }

        const name = document.createElement('div');
        name.className = 'city-name';
        name.textContent = place.name;

        const meta = document.createElement('div');
        meta.className = 'city-meta';

        const date = document.createElement('div');
        date.className = 'city-date';
        if (place.visitDate) {
            const d = new Date(place.visitDate);
            const month = d.toLocaleString(undefined, { month: 'short' });
            date.textContent = `${month} ${d.getFullYear()}`;
        } else {
            date.textContent = '';
        }

        meta.appendChild(name);
        meta.appendChild(date);

        card.appendChild(cover);
        card.appendChild(meta);

        card.addEventListener('click', () => openCarousel(place.id));
        list.appendChild(card);
    });
}

let carouselState = { place: null, index: 0 };
const photoCarouselModal = document.getElementById('photoCarouselModal');
const closeCarousel = document.getElementById('closeCarousel');
const prevPhoto = document.getElementById('prevPhoto');
const nextPhoto = document.getElementById('nextPhoto');
const carouselImage = document.getElementById('carouselImage');
const carouselCaption = document.getElementById('carouselCaption');

function openCarousel(placeId) {
    const place = state.places.find(p => p.id === placeId);
    if (!place) return;
    carouselState.place = place;
    carouselState.index = 0;
    renderCarouselImage();
    photoCarouselModal.classList.remove('hidden');
    photoCarouselModal.setAttribute('aria-hidden', 'false');
}

function closeCarouselModal() {
    photoCarouselModal.classList.add('hidden');
    photoCarouselModal.setAttribute('aria-hidden', 'true');
}

function renderCarouselImage() {
    const photos = carouselState.place.photos || [];
    if (!photos.length) {
        carouselImage.src = '';
        carouselCaption.textContent = 'No hay fotos';
        return;
    }
    const current = photos[carouselState.index];
    carouselImage.src = current.imageUrl;
    carouselImage.alt = current.title || '';
    carouselCaption.textContent = `${current.title || ''} — ${current.description || ''}`;
}

function prevCarousel() {
    const photos = carouselState.place.photos || [];
    if (!photos.length) return;
    carouselState.index = (carouselState.index - 1 + photos.length) % photos.length;
    renderCarouselImage();
}

function nextCarousel() {
    const photos = carouselState.place.photos || [];
    if (!photos.length) return;
    carouselState.index = (carouselState.index + 1) % photos.length;
    renderCarouselImage();
}

// wire carousel controls
if (closeCarousel) closeCarousel.addEventListener('click', closeCarouselModal);
if (prevPhoto) prevPhoto.addEventListener('click', prevCarousel);
if (nextPhoto) nextPhoto.addEventListener('click', nextCarousel);

// markers and map coordinate helpers removed

function populatePlaceSelect() {
    placeSelect.innerHTML = '';
    state.places.forEach((place) => {
        const option = document.createElement('option');
        option.value = place.id;
        option.textContent = place.name;
        placeSelect.appendChild(option);
    });

    if (state.activePlaceId) {
        placeSelect.value = state.activePlaceId;
    }
}

function renderEmptyState(message = 'Todavía no hay lugares para mostrar.') {
    const list = document.getElementById('cityList');
    list.innerHTML = `
        <div class="empty-state">
            <h3>Sin recuerdos aún</h3>
            <p>${message}</p>
        </div>
    `;
}

function openModal(type) {
    state.modalType = type;
    modalBackdrop.classList.remove('hidden');
    modalBackdrop.setAttribute('aria-hidden', 'false');

        if (type === 'place') {
        modalTitle.textContent = 'Añadir un lugar visitado';
        placeForm.classList.remove('hidden');
        photoForm.classList.add('hidden');
    } else {
        modalTitle.textContent = 'Añadir una foto';
        photoForm.classList.remove('hidden');
        placeForm.classList.add('hidden');
    }
}

function closeModal() {
    state.modalType = null;
    modalBackdrop.classList.add('hidden');
    modalBackdrop.setAttribute('aria-hidden', 'true');
    placeForm.classList.add('hidden');
    photoForm.classList.add('hidden');
    placeForm.reset();
    photoForm.reset();
}



async function handlePlaceSubmit(event) {
    event.preventDefault();
    const formData = new FormData(placeForm);
    const rawVisit = formData.get('visitDate') || '';
    const coverFile = formData.get('coverFile');
    const visitDate = rawVisit ? `${rawVisit}-01` : null;

    const payload = {
        name: formData.get('name'),
        country: formData.get('country'),
        description: formData.get('description') || '',
        visitDate: visitDate,
        coverImageUrl: ''
    };

    try {
        const response = await fetch('/api/places', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error('No se pudo guardar el lugar');
        }

        const createdPlace = await response.json();

        if (coverFile && coverFile.size > 0) {
            const uploadFormData = new FormData();
            uploadFormData.append('title', 'Portada');
            uploadFormData.append('description', 'Imagen de portada');
            uploadFormData.append('file', coverFile);

            const coverResponse = await fetch(`/api/places/${createdPlace.id}/cover-image`, {
                method: 'POST',
                body: uploadFormData
            });

            if (!coverResponse.ok) {
                throw new Error('No se pudo guardar la imagen de portada');
            }
        }

        closeModal();
        await loadPlaces();
    } catch (error) {
        renderEmptyState(error.message);
    }
}

async function handlePhotoSubmit(event) {
    event.preventDefault();
    const formData = new FormData(photoForm);
    const placeId = formData.get('placeId');
    const file = formData.get('file');

    const uploadData = new FormData();
    uploadData.append('title', formData.get('title'));
    uploadData.append('description', formData.get('description') || '');
    uploadData.append('file', file);

    try {
        const response = await fetch(`/api/places/${placeId}/photos`, {
            method: 'POST',
            body: uploadData
        });

        if (!response.ok) {
            throw new Error('No se pudo guardar la foto');
        }

        closeModal();
        await loadPlaces();
    } catch (error) {
        renderEmptyState(error.message);
    }
}

init();
