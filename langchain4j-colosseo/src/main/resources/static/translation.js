/**
 * LangChain4j Colosseo - Translation and Grammar Analysis JavaScript
 */

// Supported languages configuration
const SUPPORTED_LANGUAGES = [
    { code: 'en', name: 'English', native: 'English' },
    { code: 'zh', name: 'Chinese', native: '中文' },
    { code: 'ja', name: 'Japanese', native: '日本語' },
    { code: 'ko', name: 'Korean', native: '한국어' },
    { code: 'fr', name: 'French', native: 'Français' },
    { code: 'de', name: 'German', native: 'Deutsch' },
    { code: 'es', name: 'Spanish', native: 'Español' },
    { code: 'it', name: 'Italian', native: 'Italiano' },
    { code: 'pt', name: 'Portuguese', native: 'Português' },
    { code: 'ru', name: 'Russian', native: 'Русский' },
    { code: 'ar', name: 'Arabic', native: 'العربية' }
];

// DOM elements
const sourceText = document.getElementById('sourceText');
const charCounter = document.getElementById('charCounter');
const languageGrid = document.getElementById('languageGrid');
const translateBtn = document.getElementById('translateBtn');
const loadingSection = document.getElementById('loadingSection');
const resultsSection = document.getElementById('resultsSection');
const errorAlert = document.getElementById('errorAlert');
const errorMessage = document.getElementById('errorMessage');

// API configuration
const API_BASE_URL = window.location.origin + '/api';

/**
 * Initialize the application
 */
function initializeApp() {
    populateLanguageGrid();
    setupEventListeners();
    updateCharCounter();
}

/**
 * Populate the language selection grid
 */
function populateLanguageGrid() {
    languageGrid.innerHTML = '';
    
    SUPPORTED_LANGUAGES.forEach(lang => {
        const languageOption = document.createElement('div');
        languageOption.className = 'language-option';
        languageOption.innerHTML = `
            <input type="checkbox" id="lang-${lang.code}" value="${lang.code}">
            <label for="lang-${lang.code}" class="mb-0">
                <strong>${lang.native}</strong> (${lang.name})
            </label>
        `;
        languageGrid.appendChild(languageOption);
    });
}

/**
 * Setup event listeners
 */
function setupEventListeners() {
    sourceText.addEventListener('input', updateCharCounter);
    translateBtn.addEventListener('click', handleTranslate);
    
    // Add enter key support for textarea
    sourceText.addEventListener('keydown', (e) => {
        if (e.ctrlKey && e.key === 'Enter') {
            handleTranslate();
        }
    });
}

/**
 * Update character counter
 */
function updateCharCounter() {
    const length = sourceText.value.length;
    const maxLength = 10000;
    
    charCounter.textContent = `${length} / ${maxLength} characters`;
    
    // Update counter styling based on length
    charCounter.classList.remove('warning', 'danger');
    if (length > maxLength * 0.9) {
        charCounter.classList.add('danger');
    } else if (length > maxLength * 0.75) {
        charCounter.classList.add('warning');
    }
}

/**
 * Get selected languages
 */
function getSelectedLanguages() {
    const checkboxes = document.querySelectorAll('#languageGrid input[type="checkbox"]:checked');
    return Array.from(checkboxes).map(cb => cb.value);
}

/**
 * Show error message
 */
function showError(message) {
    errorMessage.textContent = message;
    errorAlert.style.display = 'block';
    setTimeout(() => {
        errorAlert.style.display = 'none';
    }, 5000);
}

/**
 * Show loading state
 */
function showLoading() {
    loadingSection.style.display = 'block';
    resultsSection.style.display = 'none';
    translateBtn.disabled = true;
    translateBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Translating...';
}

/**
 * Hide loading state
 */
function hideLoading() {
    loadingSection.style.display = 'none';
    translateBtn.disabled = false;
    translateBtn.innerHTML = '<i class="fas fa-language"></i> Translate';
}

/**
 * Handle translation request
 */
async function handleTranslate() {
    const text = sourceText.value.trim();
    const targetLanguages = getSelectedLanguages();
    
    // Validation
    if (!text) {
        showError('Please enter text to translate.');
        return;
    }
    
    if (targetLanguages.length === 0) {
        showError('Please select at least one target language.');
        return;
    }
    
    if (text.length > 10000) {
        showError('Text exceeds maximum length of 10,000 characters.');
        return;
    }
    
    showLoading();
    
    try {
        const response = await fetch(`${API_BASE_URL}/translateText`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                sourceText: text,
                targetLanguages: targetLanguages
            })
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Translation failed');
        }
        
        const result = await response.json();
        displayTranslations(result);
        
    } catch (error) {
        logger.error('Translation error:', error);
        showError(error.message || 'Translation service is temporarily unavailable. Please try again.');
    } finally {
        hideLoading();
    }
}

/**
 * Display translation results
 */
function displayTranslations(result) {
    resultsSection.innerHTML = '';
    resultsSection.style.display = 'block';
    
    // Add source text section
    const sourceSection = document.createElement('div');
    sourceSection.className = 'translation-result';
    sourceSection.innerHTML = `
        <div class="translation-header">
            <span class="language-badge">Source</span>
        </div>
        <div class="translation-text">${escapeHtml(result.sourceText)}</div>
    `;
    resultsSection.appendChild(sourceSection);
    
    // Add translation sections
    Object.entries(result.translations).forEach(([langCode, translation]) => {
        const lang = SUPPORTED_LANGUAGES.find(l => l.code === langCode);
        const translationSection = document.createElement('div');
        translationSection.className = 'translation-result';
        translationSection.innerHTML = `
            <div class="translation-header d-flex justify-content-between align-items-center">
                <span class="language-badge">${lang.native} (${lang.name})</span>
                <div>
                    <button class="btn btn-analyze me-2" onclick="analyzeGrammar('${langCode}', '${escapeHtml(translation)}')">
                        <i class="fas fa-search"></i> Analyze Grammar
                    </button>
                    <button class="btn btn-copy" onclick="copyToClipboard('${escapeHtml(translation)}')">
                        <i class="fas fa-copy"></i> Copy
                    </button>
                </div>
            </div>
            <div class="translation-text">${escapeHtml(translation)}</div>
            <div class="grammar-analysis" id="grammar-${langCode}" style="display: none;">
                <h6><i class="fas fa-chart-line"></i> Grammar Analysis</h6>
                <div class="grammar-components" id="components-${langCode}">
                    <div class="text-center">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Analyzing...</span>
                        </div>
                    </div>
                </div>
            </div>
        `;
        resultsSection.appendChild(translationSection);
    });
}

/**
 * Analyze grammar for a specific translation
 */
async function analyzeGrammar(languageCode, text) {
    const grammarSection = document.getElementById(`grammar-${languageCode}`);
    const componentsDiv = document.getElementById(`components-${languageCode}`);
    
    // Toggle visibility
    if (grammarSection.style.display === 'none') {
        grammarSection.style.display = 'block';
        
        try {
            const response = await fetch(`${API_BASE_URL}/analyzeGrammar`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    text: text,
                    languageCode: languageCode
                })
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Grammar analysis failed');
            }
            
            const result = await response.json();
            displayGrammarComponents(componentsDiv, result);
            
        } catch (error) {
            logger.error('Grammar analysis error:', error);
            componentsDiv.innerHTML = `
                <div class="alert alert-warning">
                    <i class="fas fa-exclamation-triangle"></i>
                    Grammar analysis failed: ${error.message}
                </div>
            `;
        }
    } else {
        grammarSection.style.display = 'none';
    }
}

/**
 * Display grammar components
 */
function displayGrammarComponents(container, analysis) {
    let html = '<div class="mb-3"><strong>Text:</strong> ' + escapeHtml(analysis.text) + '</div>';
    html += '<div class="mb-3"><strong>Components:</strong></div>';
    
    analysis.components.forEach(component => {
        const features = Object.entries(component.features || {})
            .map(([key, value]) => `<span class="badge bg-secondary me-1">${key}: ${value}</span>`)
            .join('');
        
        html += `
            <div class="border rounded p-2 mb-2">
                <div class="d-flex align-items-center mb-1">
                    <span class="grammar-component" style="background-color: ${component.color}20; color: ${component.color}; border-color: ${component.color};">
                        ${escapeHtml(component.text)}
                    </span>
                    <span class="badge bg-primary ms-2">${component.componentType}</span>
                </div>
                <div class="small text-muted">
                    Position: ${component.startIndex}-${component.endIndex}
                    ${features ? '<br>Features: ' + features : ''}
                </div>
            </div>
        `;
    });
    
    container.innerHTML = html;
}

/**
 * Copy text to clipboard
 */
async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
        
        // Show temporary success message
        const btn = event.target.closest('button');
        const originalText = btn.innerHTML;
        btn.innerHTML = '<i class="fas fa-check"></i> Copied!';
        btn.classList.add('btn-success');
        btn.classList.remove('btn-copy');
        
        setTimeout(() => {
            btn.innerHTML = originalText;
            btn.classList.remove('btn-success');
            btn.classList.add('btn-copy');
        }, 2000);
        
    } catch (error) {
        logger.error('Copy to clipboard failed:', error);
        showError('Failed to copy text to clipboard.');
    }
}

/**
 * Escape HTML to prevent XSS
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * Simple logger
 */
const logger = {
    error: (message, error) => {
        console.error(`[LangChain4j Colosseo] ${message}`, error);
    },
    info: (message) => {
        console.info(`[LangChain4j Colosseo] ${message}`);
    }
};

// Initialize the application when DOM is loaded
document.addEventListener('DOMContentLoaded', initializeApp);