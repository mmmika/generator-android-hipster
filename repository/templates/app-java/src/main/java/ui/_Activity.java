package <%= appPackage %>.ui.<%= activityPackageName %>;

import android.os.Bundle;
import <%= appPackage %>.di.ActivityScope;
import <%= appPackage %>.di.HasComponent;
import <%= appPackage %>.ui.base.BaseActivity;
import <%= appPackage %>.R;
import <%= appPackage %>.application.App;
<% if (componentType == 'createNew') { %>
import <%= appPackage %>.di.components.Dagger<%= activityName %>Component;
import <%= appPackage %>.di.components.<%= activityName %>Component;
import <%= appPackage %>.di.modules.<%= activityName %>Module;
<% } else if (componentType == 'useApplication') { %>
import <%= appPackage %>.application.App;
import <%= appPackage %>.di.components.ApplicationComponent;
<% } %>

<% if (nucleus == true) { %>import nucleus.factory.PresenterFactory; <% } %>

import javax.inject.Inject;

@ActivityScope
public class <%= activityName %>Activity extends BaseActivity<<%= activityName %>Presenter> implements <%= activityName %>View, HasComponent<<% if (componentType == 'createNew') { %><%= activityName %><% } else { %>Application<% } %>Component> {

        @Inject
        <%= activityName %>Presenter <%= activityName.toLowerCase() %>Presenter;

        <% if (componentType == 'createNew') { %><%= activityName %>Component component;<% } else { %>ApplicationComponent component;<% } %>

        protected void injectModule() {
            <% if (componentType == 'useApplication') { %>component = App.graph.inject(this);<% } else { %>component = Dagger<%= activityName %>Component.builder().applicationComponent(App.graph).<%= activityName.toLowerCase() %>Module(new <%= activityName %>Module(this)).build();
            component.inject(this);<% } %>
        }
          <% if (nucleus == true) { %>
        public PresenterFactory<<%= activityName %>Presenter> getPresenterFactory() {
                return () -> <%= activityName.toLowerCase() %>Presenter;
        }<% } %>

        public void onCreate(Bundle savedInstanceState ) {
                super.onCreate(savedInstanceState);
        }

        protected int getLayoutResource() {
                return R.layout.activity_<%= activityName.toLowerCase() %>;
        }

        @Override
        public <% if (componentType == 'createNew') { %><%= activityName %><% } else { %>Application<% } %>Component getComponent() {
          return component;
        }

}