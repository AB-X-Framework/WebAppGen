
let workingEnv = {"shouldUpdate": false};
/**
 * Check if the value exists otherwise add it and select it
 * Return true if it exists
 * @param selectContainer
 * @param valueToSelect
 * @param label
 */
function selectOrAddValue(selectContainer, valueToSelect, label = valueToSelect) {
    // Check if the value already exists
    let exists = selectContainer.find('option').is(function () {
        return $(this).val() === valueToSelect;
    });
    if (!exists) {
        let inserted = false;
        let newOption = $('<option>', {
            value: valueToSelect,
            text: label,
            selected: true
        });
        // Find correct alphabetical insert point
        selectContainer.find('option').each(function () {
            if (!inserted && $(this).val().localeCompare(valueToSelect) > 0) {
                $(this).before(newOption);
                inserted = true;
                return false; // Break the loop
            }
        });
        // If not inserted, append at the end
        if (!inserted) {
            selectContainer.append(newOption);
        }
    } else {
        // Just select the existing value
        selectContainer.val(valueToSelect);
    }
    // Re-initialize Materialize select to update UI
    selectContainer.formSelect();
    return exists;
}

/**
 * Return if exists
 * @param autocomplete
 * @param newValue
 * @returns {boolean}
 */
function addAutocompleteValue(autocomplete, newValue) {
    if (typeof autocomplete._data[newValue] === "undefined") {
        autocomplete._data[newValue] = null; // Or provide an image URL instead of null
        M.Autocomplete.getInstance(autocomplete)
            .updateData(autocomplete._data);
        $(autocomplete).val(newValue);
        M.updateTextFields();
        return false;
    } else {
        $(autocomplete).val(newValue);
        M.updateTextFields();
        return true;
    }
}

function clearAutocomplete(autocomplete){
    M.Autocomplete.getInstance(autocomplete).updateData({});
    $(LeftPanel.MapName).val("").trigger("change");
    M.updateTextFields();
}