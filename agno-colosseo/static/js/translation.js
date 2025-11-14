// ============================================================================
// TranslationAPIClient class
// ============================================================================
class TranslationAPIClient {
    constructor() {
        this.baseURL = window.location.origin;
    }

    /**
     * Translate text to multiple target languages
     * @param {Object} request - Translation request with sourceText and targetLanguages
     * @returns {Promise<Object>} Translation result
     */
    async translate(request) {
        try {
            const response = await fetch(`${this.baseURL}/api/translateText`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(request)
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                throw new Error('Network connection failed');
            }
            throw error;
        }
    }

    /**
     * Analyze grammar of translated text
     * @param {string} text - Text to analyze
     * @param {string} languageCode - Language code
     * @returns {Promise<Object>} Grammar analysis result
     */
    async analyze(text, languageCode) {
        try {
            const response = await fetch(`${this.baseURL}/api/analyzeGrammar`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ text, languageCode })
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                throw new Error('Network connection failed');
            }
            throw error;
        }
    }
}

// ============================================================================
// TranslationUI class for user interactions
// ============================================================================
class TranslationUI {
    constructor() {
        this.apiClient = new TranslationAPIClient();
        this.currentTranslations = null;
        this.languageNames = {
            'en': { native: 'English', english: 'English' },
            'zh': { native: '中文', english: 'Chinese' },
            'ja': { native: '日本語', english: 'Japanese' },
            'ko': { native: '한국어', english: 'Korean' },
            'fr': { native: 'Français', english: 'French' },
            'de': { native: 'Deutsch', english: 'German' },
            'es': { native: 'Español', english: 'Spanish' },
            'it': { native: 'Italiano', english: 'Italian' },
            'pt': { native: 'Português', english: 'Portuguese' },
            'ru': { native: 'Русский', english: 'Russian' },
            'ar': { native: 'العربية', english: 'Arabic' },
            'hi': { native: 'हिन्दी', english: 'Hindi' }
        };
        this.initializeEventListeners();
    }

    initializeEventListeners() {
        document.getElementById('translate-btn').addEventListener('click', () => {
            this.handleTranslate();
        });
    }

    /**
     * Handle translation request
     */
    async handleTranslate() {
        // Collect form data
        const sourceText = document.getElementById('source-text').value.trim();
        const selectedLanguages = Array.from(
            document.querySelectorAll('.language-option input[type="checkbox"]:checked')
        ).map(cb => cb.value);

        // Validate input
        if (!sourceText) {
            this.displayError('Please enter text to translate');
            return;
        }

        if (selectedLanguages.length === 0) {
            this.displayError('Please select at least one target language');
            return;
        }

        // Show loading state
        this.setLoadingState(true);
        this.hideError();

        try {
            // Call API
            const result = await this.apiClient.translate({
                sourceText: sourceText,
                targetLanguages: selectedLanguages
            });

            // Store results and display
            this.currentTranslations = result;
            this.displayTranslations(result);
            this.enableAnalysisButtons();
        } catch (error) {
            this.displayError(error.message);
        } finally {
            this.setLoadingState(false);
        }
    }

    /**
     * Display translation results
     * @param {Object} result - Translation result object
     */
    displayTranslations(result) {
        const resultsContainer = document.getElementById('translation-results');
        resultsContainer.innerHTML = '';

        // Render each translation
        Object.entries(result.translations).forEach(([langCode, translatedText]) => {
            const row = this.createTranslationRow(langCode, translatedText);
            resultsContainer.appendChild(row);
        });

        // Show results section
        document.getElementById('results-section').classList.add('visible');
    }

    /**
     * Create a translation result row
     * @param {string} langCode - Language code
     * @param {string} translatedText - Translated text
     * @returns {HTMLElement} Translation row element
     */
    createTranslationRow(langCode, translatedText) {
        const row = document.createElement('div');
        row.className = 'translation-row';
        row.dataset.language = langCode;

        const langInfo = this.languageNames[langCode];
        const languageLabel = `${langInfo.english}`;

        row.innerHTML = `
            <div class="translation-header">
                <div class="language-label">${languageLabel}</div>
                <div class="translation-actions">
                    <button class="copy-btn" data-lang="${langCode}">Copy</button>
                    <button class="analyze-btn" data-lang="${langCode}" disabled>Analyze</button>
                </div>
            </div>
            <div class="translation-text">${this.escapeHtml(translatedText)}</div>
            <div class="analysis-result" id="analysis-${langCode}" style="display: none;"></div>
        `;

        // Add copy functionality
        const copyBtn = row.querySelector('.copy-btn');
        copyBtn.addEventListener('click', () => {
            this.copyToClipboard(translatedText, copyBtn);
        });

        // Add analyze functionality
        const analyzeBtn = row.querySelector('.analyze-btn');
        analyzeBtn.addEventListener('click', () => {
            this.handleAnalyze(langCode);
        });

        return row;
    }

    /**
     * Enable analysis buttons after translations complete
     */
    enableAnalysisButtons() {
        document.querySelectorAll('.analyze-btn').forEach(btn => {
            btn.disabled = false;
        });
    }

    /**
     * Copy text to clipboard
     * @param {string} text - Text to copy
     * @param {HTMLElement} button - Copy button element
     */
    async copyToClipboard(text, button) {
        try {
            await navigator.clipboard.writeText(text);
            const originalText = button.textContent;
            button.textContent = 'Copied!';
            button.classList.add('copied');
            
            setTimeout(() => {
                button.textContent = originalText;
                button.classList.remove('copied');
            }, 2000);
        } catch (error) {
            this.displayError('Copy failed');
        }
    }

    /**
     * Handle grammar analysis request
     * @param {string} languageCode - Language code to analyze
     */
    async handleAnalyze(languageCode) {
        if (!this.currentTranslations || !this.currentTranslations.translations[languageCode]) {
            this.displayError('Translation result not found');
            return;
        }

        const text = this.currentTranslations.translations[languageCode];
        const analyzeBtn = document.querySelector(`.analyze-btn[data-lang="${languageCode}"]`);

        // Show loading state
        analyzeBtn.classList.add('loading');
        analyzeBtn.disabled = true;
        this.hideError();

        try {
            // Call API
            const analysis = await this.apiClient.analyze(text, languageCode);
            
            // Display analysis with languageCode parameter
            this.displayAnalysis(analysis, languageCode);
        } catch (error) {
            this.displayError(error.message);
        } finally {
            analyzeBtn.classList.remove('loading');
            analyzeBtn.disabled = false;
        }
    }

    /**
     * Display grammar analysis with color highlighting
     * @param {Object} analysis - Grammar analysis result
     * @param {string} languageCode - Language code for the analysis
     */
    displayAnalysis(analysis, languageCode) {
        // Find the analysis container for this specific language
        const analysisContainer = document.getElementById(`analysis-${languageCode}`);
        if (!analysisContainer) {
            this.displayError('Analysis container not found');
            return;
        }

        // Build legend
        const componentTypes = new Set();
        analysis.components.forEach(comp => {
            componentTypes.add(comp.componentType);
        });

        let legendHTML = '';
        componentTypes.forEach(type => {
            const component = analysis.components.find(c => c.componentType === type);
            legendHTML += `
                <div class="legend-item">
                    <div class="legend-color" style="background-color: ${component.color}"></div>
                    <span>${this.formatComponentType(type)}</span>
                </div>
            `;
        });

        // Build highlighted text
        let highlightedText = '';
        analysis.components.forEach(comp => {
            const features = Object.entries(comp.features || {})
                .map(([key, value]) => `${key}: ${value}`)
                .join(', ');
            const title = features ? `${comp.componentType} (${features})` : comp.componentType;
            
            highlightedText += `<span class="grammar-component" style="background-color: ${comp.color}" title="${title}">${this.escapeHtml(comp.text)}</span>`;
        });

        // Set the analysis content
        analysisContainer.innerHTML = `
            <h3>Grammar Analysis</h3>
            <div class="analysis-legend">${legendHTML}</div>
            <div class="analyzed-text">${highlightedText}</div>
        `;

        // Show the analysis result
        analysisContainer.style.display = 'block';
        
        // Scroll to the analysis result
        analysisContainer.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }

    /**
     * Format component type for display (English only)
     * @param {string} type - Component type
     * @returns {string} Formatted type
     */
    formatComponentType(type) {
        return type.split('_').map(word => 
            word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
        ).join(' ');
    }

    /**
     * Escape HTML to prevent XSS
     * @param {string} text - Text to escape
     * @returns {string} Escaped text
     */
    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    // ============================================================================
    // UI state management
    // ============================================================================

    /**
     * Set loading state for translation
     * @param {boolean} isLoading - Loading state
     */
    setLoadingState(isLoading) {
        const translateBtn = document.getElementById('translate-btn');
        const loadingIndicator = document.getElementById('loading-indicator');

        if (isLoading) {
            translateBtn.classList.add('loading');
            translateBtn.disabled = true;
            loadingIndicator.classList.add('visible');
        } else {
            translateBtn.classList.remove('loading');
            translateBtn.disabled = false;
            loadingIndicator.classList.remove('visible');
        }
    }

    /**
     * Clear/reset functionality
     */
    handleClear() {
        // Reset form
        document.getElementById('source-text').value = '';
        document.getElementById('char-counter').textContent = '0 / 10000';
        document.getElementById('char-counter').classList.remove('warning', 'error');
        
        document.querySelectorAll('.language-option input[type="checkbox"]').forEach(cb => {
            cb.checked = false;
            cb.closest('.language-option').classList.remove('selected');
        });

        // Clear state
        this.currentTranslations = null;

        // Hide sections
        document.getElementById('results-section').classList.remove('visible');
        this.hideError();

        // Disable analysis buttons and clear inline analysis results
        document.querySelectorAll('.analyze-btn').forEach(btn => {
            btn.disabled = true;
        });

        // Clear all inline analysis results
        document.querySelectorAll('.analysis-result').forEach(analysisDiv => {
            analysisDiv.style.display = 'none';
            analysisDiv.innerHTML = '';
        });
    }

    /**
     * Display error message
     * @param {string} message - Error message
     */
    displayError(message) {
        const errorDiv = document.getElementById('error-message');
        errorDiv.textContent = message;
        errorDiv.classList.add('visible');

        // Auto-hide after 5 seconds
        setTimeout(() => {
            this.hideError();
        }, 5000);
    }

    /**
     * Hide error message
     */
    hideError() {
        const errorDiv = document.getElementById('error-message');
        errorDiv.classList.remove('visible');
    }
}

// Initialize the UI when DOM is ready
window.translationUI = new TranslationUI();
