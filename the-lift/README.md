# [The Lift kata](https://kata-log.rocks/lift-kata)
## Requirements
Since lifts are everywhere and they contain software, 
how easy would it be to write a basic one? 
Let’s TDD a lift, starting with simple behaviors and working toward complex ones. 
Assume good input from calling code and concentrate on the main flow.

Here are some suggested lift features:
* a lift responds to calls containing a source floor and direction
* a lift has an attribute floor, which describes it’s current location
* a lift delivers passengers to requested floors
* you may implement current floor monitor
* you may implement direction arrows
* you may implement doors (opening and closing)
* you may implement DING!
* there can be more than one lift