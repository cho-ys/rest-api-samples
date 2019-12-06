```python
from mstrdata import microstrategy
PROJECT_NAME = "MicroStrategy Tutorial" # MicroStrategy Project Name
USER_NAME = "guest"
PASSWORD = ""
BASE_URL = "https://demo.microstrategy.com/MicroStrategyLibrary2/api" # URL to server instance
REPORT_ID = '2627169745E4D76AC2A16980DCF35B9A' # MicroStrategy report ID (Individual Sales Report)
CUBE_ID = 'DF05CEA04CCA432397E466A55F1A9EC7' # MicroStrategy cube ID (Order Cube)

# Create the connection object
conn = microstrategy.Connection(base_url=BASE_URL, username=USER_NAME, password=PASSWORD,
                                login_mode=1, project_name=PROJECT_NAME)
```


```python
conn.connect() # connect to server in the Mobile Dossier Project

# retrieve our report with raw metric values
df = conn.get_report(REPORT_ID, limit=1000, raw=True)
df.head(10)
```




<div>
<style scoped>
    .dataframe tbody tr th:only-of-type {
        vertical-align: middle;
    }

    .dataframe tbody tr th {
        vertical-align: top;
    }

    .dataframe thead th {
        text-align: right;
    }
</style>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th></th>
      <th></th>
      <th>Sales</th>
      <th>Rank by Region</th>
    </tr>
    <tr>
      <th>Region</th>
      <th>Employee@Last Name</th>
      <th>Employee@First Name</th>
      <th></th>
      <th></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td rowspan="6" valign="top">Northeast</td>
      <td>Sawyer</td>
      <td>Leanne</td>
      <td>302400.30</td>
      <td>1.0</td>
    </tr>
    <tr>
      <td>Kelly</td>
      <td>Laura</td>
      <td>278238.40</td>
      <td>2.0</td>
    </tr>
    <tr>
      <td>Yager</td>
      <td>Beth</td>
      <td>263247.10</td>
      <td>3.0</td>
    </tr>
    <tr>
      <td>Kieferson</td>
      <td>Jack</td>
      <td>62033.45</td>
      <td>4.0</td>
    </tr>
    <tr>
      <td>De Le Torre</td>
      <td>Sandra</td>
      <td>61800.50</td>
      <td>5.0</td>
    </tr>
    <tr>
      <td>Sonder</td>
      <td>Melanie</td>
      <td>40269.35</td>
      <td>6.0</td>
    </tr>
    <tr>
      <td rowspan="4" valign="top">Mid-Atlantic</td>
      <td>Bernstein</td>
      <td>Lawrence</td>
      <td>138064.05</td>
      <td>1.0</td>
    </tr>
    <tr>
      <td>Folks</td>
      <td>Adrienne</td>
      <td>116088.30</td>
      <td>2.0</td>
    </tr>
    <tr>
      <td>Hollywood</td>
      <td>Robert</td>
      <td>112044.25</td>
      <td>3.0</td>
    </tr>
    <tr>
      <td>Corcoran</td>
      <td>Peter</td>
      <td>36742.55</td>
      <td>4.0</td>
    </tr>
  </tbody>
</table>
</div>




```python
# Get formatted metricValues from the report

df = conn.get_report(REPORT_ID, limit=1000, raw=False)
df.head(10)
```




<div>
<style scoped>
    .dataframe tbody tr th:only-of-type {
        vertical-align: middle;
    }

    .dataframe tbody tr th {
        vertical-align: top;
    }

    .dataframe thead th {
        text-align: right;
    }
</style>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th></th>
      <th></th>
      <th>Sales</th>
      <th>Rank by Region</th>
    </tr>
    <tr>
      <th>Region</th>
      <th>Employee@Last Name</th>
      <th>Employee@First Name</th>
      <th></th>
      <th></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td rowspan="6" valign="top">Northeast</td>
      <td>Sawyer</td>
      <td>Leanne</td>
      <td>$302,400</td>
      <td>1</td>
    </tr>
    <tr>
      <td>Kelly</td>
      <td>Laura</td>
      <td>$278,238</td>
      <td>2</td>
    </tr>
    <tr>
      <td>Yager</td>
      <td>Beth</td>
      <td>$263,247</td>
      <td>3</td>
    </tr>
    <tr>
      <td>Kieferson</td>
      <td>Jack</td>
      <td>$62,033</td>
      <td>4</td>
    </tr>
    <tr>
      <td>De Le Torre</td>
      <td>Sandra</td>
      <td>$61,801</td>
      <td>5</td>
    </tr>
    <tr>
      <td>Sonder</td>
      <td>Melanie</td>
      <td>$40,269</td>
      <td>6</td>
    </tr>
    <tr>
      <td rowspan="4" valign="top">Mid-Atlantic</td>
      <td>Bernstein</td>
      <td>Lawrence</td>
      <td>$138,064</td>
      <td>1</td>
    </tr>
    <tr>
      <td>Folks</td>
      <td>Adrienne</td>
      <td>$116,088</td>
      <td>2</td>
    </tr>
    <tr>
      <td>Hollywood</td>
      <td>Robert</td>
      <td>$112,044</td>
      <td>3</td>
    </tr>
    <tr>
      <td>Corcoran</td>
      <td>Peter</td>
      <td>$36,743</td>
      <td>4</td>
    </tr>
  </tbody>
</table>
</div>




```python
# remove MultiIndexing rows
df.reset_index().head(5)
```




<div>
<style scoped>
    .dataframe tbody tr th:only-of-type {
        vertical-align: middle;
    }

    .dataframe tbody tr th {
        vertical-align: top;
    }

    .dataframe thead th {
        text-align: right;
    }
</style>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>Region</th>
      <th>Employee@Last Name</th>
      <th>Employee@First Name</th>
      <th>Sales</th>
      <th>Rank by Region</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>0</td>
      <td>Northeast</td>
      <td>Sawyer</td>
      <td>Leanne</td>
      <td>$302,400</td>
      <td>1</td>
    </tr>
    <tr>
      <td>1</td>
      <td>Northeast</td>
      <td>Kelly</td>
      <td>Laura</td>
      <td>$278,238</td>
      <td>2</td>
    </tr>
    <tr>
      <td>2</td>
      <td>Northeast</td>
      <td>Yager</td>
      <td>Beth</td>
      <td>$263,247</td>
      <td>3</td>
    </tr>
    <tr>
      <td>3</td>
      <td>Northeast</td>
      <td>Kieferson</td>
      <td>Jack</td>
      <td>$62,033</td>
      <td>4</td>
    </tr>
    <tr>
      <td>4</td>
      <td>Northeast</td>
      <td>De Le Torre</td>
      <td>Sandra</td>
      <td>$61,801</td>
      <td>5</td>
    </tr>
  </tbody>
</table>
</div>




```python
REPORT_ID = "EB3CD5D14F4C8C77782AC0882C986B8D" # Electronics Revenue vs. Forecast (cross-tab)
conn.connect()
cube_df = conn.get_report(REPORT_ID, limit=1000)
cube_df.head(10)
```




<div>
<style scoped>
    .dataframe tbody tr th:only-of-type {
        vertical-align: middle;
    }

    .dataframe tbody tr th {
        vertical-align: top;
    }

    .dataframe thead tr th {
        text-align: left;
    }

    .dataframe thead tr:last-of-type th {
        text-align: right;
    }
</style>
<table border="1" class="dataframe">
  <thead>
    <tr>
      <th></th>
      <th></th>
      <th>Quarter</th>
      <th colspan="2" halign="left">2016 Q1</th>
      <th colspan="2" halign="left">2016 Q2</th>
      <th colspan="2" halign="left">2016 Q3</th>
      <th colspan="2" halign="left">2016 Q4</th>
    </tr>
    <tr>
      <th></th>
      <th></th>
      <th>Metrics</th>
      <th>Revenue Forecast</th>
      <th>Revenue</th>
      <th>Revenue Forecast</th>
      <th>Revenue</th>
      <th>Revenue Forecast</th>
      <th>Revenue</th>
      <th>Revenue Forecast</th>
      <th>Revenue</th>
    </tr>
    <tr>
      <th>Region</th>
      <th>Category</th>
      <th>Subcategory</th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td rowspan="6" valign="top">Mid-Atlantic</td>
      <td rowspan="6" valign="top">Electronics</td>
      <td>Audio Equipment</td>
      <td>$ 42,981</td>
      <td>$44,310</td>
      <td>$ 41,706</td>
      <td>$41,293</td>
      <td>$ 33,847</td>
      <td>$43,394</td>
      <td>$ 56,800</td>
      <td>$58,257</td>
    </tr>
    <tr>
      <td>Cameras</td>
      <td>$ 66,507</td>
      <td>$64,570</td>
      <td>$ 62,414</td>
      <td>$59,442</td>
      <td>$ 47,382</td>
      <td>$58,496</td>
      <td>$ 63,112</td>
      <td>$71,314</td>
    </tr>
    <tr>
      <td>Computers</td>
      <td>$ 21,478</td>
      <td>$23,602</td>
      <td>$ 26,841</td>
      <td>$24,922</td>
      <td>$ 23,805</td>
      <td>$21,524</td>
      <td>$ 22,698</td>
      <td>$27,086</td>
    </tr>
    <tr>
      <td>Electronics - Miscellaneous</td>
      <td>$ 54,726</td>
      <td>$53,132</td>
      <td>$ 71,869</td>
      <td>$69,776</td>
      <td>$ 45,221</td>
      <td>$51,155</td>
      <td>$ 56,445</td>
      <td>$71,000</td>
    </tr>
    <tr>
      <td>TV's</td>
      <td>$ 32,455</td>
      <td>$37,305</td>
      <td>$ 50,387</td>
      <td>$45,312</td>
      <td>$ 40,355</td>
      <td>$40,721</td>
      <td>$ 65,485</td>
      <td>$62,159</td>
    </tr>
    <tr>
      <td>Video Equipment</td>
      <td>$ 64,097</td>
      <td>$62,840</td>
      <td>$ 68,752</td>
      <td>$72,371</td>
      <td>$ 75,669</td>
      <td>$67,562</td>
      <td>$ 60,800</td>
      <td>$79,477</td>
    </tr>
    <tr>
      <td rowspan="4" valign="top">Northeast</td>
      <td rowspan="4" valign="top">Electronics</td>
      <td>Audio Equipment</td>
      <td>$ 81,103</td>
      <td>$80,300</td>
      <td>$ 90,735</td>
      <td>$88,092</td>
      <td>$ 76,754</td>
      <td>$84,345</td>
      <td>$ 128,767</td>
      <td>$121,478</td>
    </tr>
    <tr>
      <td>Cameras</td>
      <td>$ 88,445</td>
      <td>$93,100</td>
      <td>$ 113,396</td>
      <td>$136,622</td>
      <td>$ 118,484</td>
      <td>$126,047</td>
      <td>$ 105,960</td>
      <td>$136,723</td>
    </tr>
    <tr>
      <td>Computers</td>
      <td>$ 42,604</td>
      <td>$40,575</td>
      <td>$ 47,092</td>
      <td>$46,765</td>
      <td>$ 39,623</td>
      <td>$50,994</td>
      <td>$ 44,414</td>
      <td>$57,531</td>
    </tr>
    <tr>
      <td>Electronics - Miscellaneous</td>
      <td>$ 92,489</td>
      <td>$100,532</td>
      <td>$ 116,812</td>
      <td>$104,858</td>
      <td>$ 98,638</td>
      <td>$129,447</td>
      <td>$ 134,017</td>
      <td>$129,987</td>
    </tr>
  </tbody>
</table>
</div>




```python
# Retrieve cube (A large cube, might take a few seconds)
conn.connect()
cube_df = conn.get_cube(CUBE_ID, limit=1000)
cube_df.head(5)
```




<div>
<style scoped>
    .dataframe tbody tr th:only-of-type {
        vertical-align: middle;
    }

    .dataframe tbody tr th {
        vertical-align: top;
    }

    .dataframe thead th {
        text-align: right;
    }
</style>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th>MAX_Profit</th>
      <th>MAX_Revenue</th>
      <th>MIN_Profit</th>
      <th>MIN_Revenue</th>
      <th>Month Index</th>
      <th>Month of Year</th>
      <th>N_Profit</th>
      <th>N_Revenue</th>
      <th>Quarter Index</th>
      <th>Quarter of Year</th>
      <th>SUM_Profit</th>
      <th>SUM_Revenue</th>
      <th>USS_Profit</th>
      <th>USS_Revenue</th>
    </tr>
    <tr>
      <th>Call Center</th>
      <th>Customer@Last Name</th>
      <th>Customer@First Name</th>
      <th>Customer Age</th>
      <th>Income Bracket</th>
      <th>Month</th>
      <th>Order</th>
      <th>Quarter</th>
      <th>Region</th>
      <th>Year</th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td rowspan="5" valign="top">Atlanta</td>
      <td rowspan="5" valign="top">Abraha</td>
      <td rowspan="5" valign="top">Christy</td>
      <td rowspan="5" valign="top">47</td>
      <td rowspan="5" valign="top">20K and Under</td>
      <td rowspan="2" valign="top">Aug 2016</td>
      <td>133128</td>
      <td>2016 Q3</td>
      <td>Southeast</td>
      <td>2016</td>
      <td>5</td>
      <td>35</td>
      <td>5</td>
      <td>35</td>
      <td>80</td>
      <td>8</td>
      <td>1</td>
      <td>1</td>
      <td>27</td>
      <td>3</td>
      <td>5</td>
      <td>35</td>
      <td>28</td>
      <td>1,232</td>
    </tr>
    <tr>
      <td>133797</td>
      <td>2016 Q3</td>
      <td>Southeast</td>
      <td>2016</td>
      <td>0</td>
      <td>131</td>
      <td>0</td>
      <td>131</td>
      <td>80</td>
      <td>8</td>
      <td>1</td>
      <td>1</td>
      <td>27</td>
      <td>3</td>
      <td>0</td>
      <td>131</td>
      <td>0</td>
      <td>17,266</td>
    </tr>
    <tr>
      <td>Sep 2016</td>
      <td>140740</td>
      <td>2016 Q3</td>
      <td>Southeast</td>
      <td>2016</td>
      <td>(1)</td>
      <td>23</td>
      <td>(1)</td>
      <td>23</td>
      <td>81</td>
      <td>9</td>
      <td>1</td>
      <td>1</td>
      <td>27</td>
      <td>3</td>
      <td>(1)</td>
      <td>23</td>
      <td>0</td>
      <td>548</td>
    </tr>
    <tr>
      <td>Oct 2016</td>
      <td>148126</td>
      <td>2016 Q4</td>
      <td>Southeast</td>
      <td>2016</td>
      <td>3</td>
      <td>11</td>
      <td>3</td>
      <td>11</td>
      <td>82</td>
      <td>10</td>
      <td>1</td>
      <td>1</td>
      <td>28</td>
      <td>4</td>
      <td>3</td>
      <td>11</td>
      <td>8</td>
      <td>121</td>
    </tr>
    <tr>
      <td>Dec 2016</td>
      <td>159862</td>
      <td>2016 Q4</td>
      <td>Southeast</td>
      <td>2016</td>
      <td>(3)</td>
      <td>130</td>
      <td>(3)</td>
      <td>130</td>
      <td>84</td>
      <td>12</td>
      <td>1</td>
      <td>1</td>
      <td>28</td>
      <td>4</td>
      <td>(3)</td>
      <td>130</td>
      <td>6</td>
      <td>16,913</td>
    </tr>
  </tbody>
</table>
</div>




```python
cube_df.shape
```




    (153990, 14)




```python
cube_df.reset_index().head(5)
```




<div>
<style scoped>
    .dataframe tbody tr th:only-of-type {
        vertical-align: middle;
    }

    .dataframe tbody tr th {
        vertical-align: top;
    }

    .dataframe thead th {
        text-align: right;
    }
</style>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>Call Center</th>
      <th>Customer@Last Name</th>
      <th>Customer@First Name</th>
      <th>Customer Age</th>
      <th>Income Bracket</th>
      <th>Month</th>
      <th>Order</th>
      <th>Quarter</th>
      <th>Region</th>
      <th>Year</th>
      <th>...</th>
      <th>Month Index</th>
      <th>Month of Year</th>
      <th>N_Profit</th>
      <th>N_Revenue</th>
      <th>Quarter Index</th>
      <th>Quarter of Year</th>
      <th>SUM_Profit</th>
      <th>SUM_Revenue</th>
      <th>USS_Profit</th>
      <th>USS_Revenue</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>0</td>
      <td>Atlanta</td>
      <td>Abraha</td>
      <td>Christy</td>
      <td>47</td>
      <td>20K and Under</td>
      <td>Aug 2016</td>
      <td>133128</td>
      <td>2016 Q3</td>
      <td>Southeast</td>
      <td>2016</td>
      <td>...</td>
      <td>80</td>
      <td>8</td>
      <td>1</td>
      <td>1</td>
      <td>27</td>
      <td>3</td>
      <td>5</td>
      <td>35</td>
      <td>28</td>
      <td>1,232</td>
    </tr>
    <tr>
      <td>1</td>
      <td>Atlanta</td>
      <td>Abraha</td>
      <td>Christy</td>
      <td>47</td>
      <td>20K and Under</td>
      <td>Aug 2016</td>
      <td>133797</td>
      <td>2016 Q3</td>
      <td>Southeast</td>
      <td>2016</td>
      <td>...</td>
      <td>80</td>
      <td>8</td>
      <td>1</td>
      <td>1</td>
      <td>27</td>
      <td>3</td>
      <td>0</td>
      <td>131</td>
      <td>0</td>
      <td>17,266</td>
    </tr>
    <tr>
      <td>2</td>
      <td>Atlanta</td>
      <td>Abraha</td>
      <td>Christy</td>
      <td>47</td>
      <td>20K and Under</td>
      <td>Sep 2016</td>
      <td>140740</td>
      <td>2016 Q3</td>
      <td>Southeast</td>
      <td>2016</td>
      <td>...</td>
      <td>81</td>
      <td>9</td>
      <td>1</td>
      <td>1</td>
      <td>27</td>
      <td>3</td>
      <td>(1)</td>
      <td>23</td>
      <td>0</td>
      <td>548</td>
    </tr>
    <tr>
      <td>3</td>
      <td>Atlanta</td>
      <td>Abraha</td>
      <td>Christy</td>
      <td>47</td>
      <td>20K and Under</td>
      <td>Oct 2016</td>
      <td>148126</td>
      <td>2016 Q4</td>
      <td>Southeast</td>
      <td>2016</td>
      <td>...</td>
      <td>82</td>
      <td>10</td>
      <td>1</td>
      <td>1</td>
      <td>28</td>
      <td>4</td>
      <td>3</td>
      <td>11</td>
      <td>8</td>
      <td>121</td>
    </tr>
    <tr>
      <td>4</td>
      <td>Atlanta</td>
      <td>Abraha</td>
      <td>Christy</td>
      <td>47</td>
      <td>20K and Under</td>
      <td>Dec 2016</td>
      <td>159862</td>
      <td>2016 Q4</td>
      <td>Southeast</td>
      <td>2016</td>
      <td>...</td>
      <td>84</td>
      <td>12</td>
      <td>1</td>
      <td>1</td>
      <td>28</td>
      <td>4</td>
      <td>(3)</td>
      <td>130</td>
      <td>6</td>
      <td>16,913</td>
    </tr>
  </tbody>
</table>
<p>5 rows × 24 columns</p>
</div>


