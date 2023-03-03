# Wasim_MVI
It is a generic template for any android project. In this template, I used UDF(Unidirectional Data Flow)+MVVM  architecture with clean architecture
approach in each module. In MVI (Model, View, Intent) architecture user interaction regard as event (or user intent)
that change the model which represents UI state. ViewModel is responsible for creating immutable state
for the ui and hold them. I use shared flow for user events, compose state for states and state contains the view events.
User events are one time event that is why I usehot observable like shared flow. Shared flow can be observed from multiple 
subscribers for this reasonit is a better option to use it for users event (if we want multiple work starting with one event). 
However, view events e.g., error event, navigation events are going to be part of the state according to the google's recommandation
(https://medium.com/androiddevelopers/viewmodel-one-off-event-antipatterns-16a1da869b95).


The core module contains the code which is common for the whole project. If we extend any feature 
then we can reuse the code of core module. So the most common functionalities are placed into core 
module. For big project it can be further divided into more modules like core-ui, core-network but 
for this small MVP(Minimum Viable Product) all of them integrated into single core module. 

Inside every feature module, the packages representing clean code architecture layers (presentation, domain, data). 
The presentation layer is responsible for drawing UI, UI related logic and holding UI states. Composable screens, view model,  
UI data models and mappers are part of the presentation layer. Domain layer is responsible for business logic. Different business
use cases and domain models are implemented in this layer. Data layer is responsible for providing data. Repository 
implementation, datasource (internal and external) should be part of data layer. 

The shared code with other feature modules are placed in the feature-shared module. Any feature module can depend on 0 to n number 
feature shared module to get similar behavior like other features. Core modules are pure library modules which are used by other modules.
They cannot depend on any other feature or feature-shared module. 
