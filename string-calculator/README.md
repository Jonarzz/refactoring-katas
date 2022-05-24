# [String Calculator kata](https://kata-log.rocks/string-calculator-kata) (incremental)
## Requirements
### Step 1
Create a simple String calculator with a method signature:

    int add(String numbers)

The method can take up to two numbers, separated by commas, and will return their sum.

For example `“”` or `“1”` or `“1,2”` as inputs.

For an empty string it will return 0.

### Step 2
Allow the `add` method to handle an unknown amount of numbers.

### Step 3
Allow the `add` method to handle new lines between numbers (instead of commas):
- the following input is OK: `“1\n2,3”` (will equal 6)
- the following input is **not** OK: `“1,\n”`

### Step 4
Support different delimiters:
To change a delimiter, the beginning of the string will contain a separate line 
that looks like this: `“//[delimiter]\n[numbers...]”`
for example `“//;\n1;2”` should return 3 where the default delimiter is ‘;’.

The first line is optional. All existing scenarios should still be supported.

### Step 5
Calling Add with a negative number will throw an exception `“Negatives not allowed”`
and the negative that was passed.
If there are multiple negatives, show all of them in the exception message.

### Step 6
Numbers bigger than 1000 should be ignored, so adding `2 + 1001 = 2`.

### Step 7
Delimiters can be of any length with the following format: 
`"//[delimiter]\n"` for example: `"//[***]\n1***2***3"` should return 6.