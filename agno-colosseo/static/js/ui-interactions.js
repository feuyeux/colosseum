// Character counter functionality
const sourceText = document.getElementById('source-text');
const charCounter = document.getElementById('char-counter');

sourceText.addEventListener('input', function() {
    const length = this.value.length;
    charCounter.textContent = `${length} / 10000`;
    
    charCounter.classList.remove('warning', 'error');
    if (length > 9000) {
        charCounter.classList.add('error');
    } else if (length > 7000) {
        charCounter.classList.add('warning');
    }
});

// Language option selection visual feedback
document.querySelectorAll('.language-option').forEach(option => {
    const checkbox = option.querySelector('input[type="checkbox"]');
    
    option.addEventListener('click', function(e) {
        if (e.target !== checkbox) {
            checkbox.checked = !checkbox.checked;
        }
        this.classList.toggle('selected', checkbox.checked);
    });

    checkbox.addEventListener('change', function() {
        option.classList.toggle('selected', this.checked);
    });
});

// Clear button functionality
document.getElementById('clear-btn').addEventListener('click', function() {
    if (window.translationUI) {
        window.translationUI.handleClear();
    }
});

// Select All button functionality
document.getElementById('select-all-btn').addEventListener('click', function() {
    document.querySelectorAll('.language-option input[type="checkbox"]').forEach(cb => {
        cb.checked = true;
        cb.closest('.language-option').classList.add('selected');
    });
});

// Deselect All button functionality
document.getElementById('deselect-all-btn').addEventListener('click', function() {
    document.querySelectorAll('.language-option input[type="checkbox"]').forEach(cb => {
        cb.checked = false;
        cb.closest('.language-option').classList.remove('selected');
    });
});
