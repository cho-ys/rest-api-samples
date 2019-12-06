import copy
import numpy as np
import pandas as pd

class DFCreator:

    def __init__(self):

        # dataframe MultiIndex rows         
        self._mi_rows = None

        # dataframe MultiIndex columns
        self._mi_cols = None

        # dataframe raw metric data 
        self._raw_data = []

        # dataframe formatted metric data
        self._formatted_data = []

        self._row_names = []
        self._col_names = []

        self._row_elements = None # stores pandas row index values
        self._column_elements = None # stores pandas column index values
        self._metric_elements = None # stores pandas data values
     

    def get_elements_for_row_name(self, row, name, form_index):
        # get_elements_for_row_name(self, json, name, form_index)
        # Gets the elements for a row name
        # EX: Region (row name) has elements "Mid-Alantic", "NorthEast", "SouthEast"
        #     Category (row name) has element "Electronics"
        #     Subcategory (row name) has element "Audio Equipment", "Cameras", ...

        row_elements = []
        for element in row['elements']:
            if 'subtotal' in element.keys():
                row_elements.append(element['formValues'][0])
            else:
                row_elements.append(element['formValues'][form_index])
        
        return row_elements
        # return [element['formValues'][form_index] for element in row['elements']]


    def get_elements_for_column_name(self, column, name, form_index):
        # Gets the elements for a column name excluding the Metric column
        # EX: Quarter (column name) has elements "2016 Q1", "2016 Q2", ...

        return [element['formValues'][form_index] for element in column['elements']]


    def map_index(self, row_elements, index_map):

        # Maps indices in index_map to their respective values in row_elements 
        index_map_copy = np.array(list(index_map))
        visited_set = []

        # expand the data mapping to also map to alternate formValues 
        for row_element in row_elements:
            if row_element['index'] in visited_set:
                col = list(map(lambda x: [x], index_map_copy[:,row_element['index']]))
                index_map_copy = np.insert(index_map_copy, [row_element['index']], col, 1)
            
            visited_set.append(row_element['index'])

        # map data indices to their respective elements
        idx = 0
        index_map_copy = index_map_copy.tolist()
        for row_idx in index_map_copy:
            for i in range(len(row_idx)):
                row_value = (row_elements[i]['elements'][row_idx[i]])
                row_idx[i] = row_value
        return index_map_copy

    def parse_rows(self, json):
        rows = json['definition']['grid']['rows']
        rows_data = json['data']['headers']['rows']
        res_row_data = []
        row_idx = 0

        for row in rows:
            cur_row_data = {}
            if len(row['forms']) == 1: # no alternate form
                row_name = row['name']
                row_elements = self.get_elements_for_row_name(row, row_name, 0)
                cur_row_data['name'] = row_name
                cur_row_data['elements'] = row_elements
                cur_row_data['index'] = row_idx
                res_row_data.append(cur_row_data)
            else:
                for i in range(len(row['forms'])):
                    cur_row_data = {}
                    row_name = row['name'] + "@" + row['forms'][i]['name']
                    row_elements = self.get_elements_for_row_name(row, row['name'], i)
                    cur_row_data['name'] = row_name
                    cur_row_data['elements'] = row_elements
                    cur_row_data['index'] = row_idx
                    res_row_data.append(cur_row_data)
            row_idx = row_idx + 1

        return res_row_data
                

    def parse_columns(self, json):
        columns = list(filter(lambda col: col['name'] != "Metrics", json['definition']['grid']['columns']))
        columns_data = json['data']['headers']['columns']
        res_column_data = []
        column_idx = 0
        
        for column in columns:
            cur_column_data = {}
            if len(column['forms']) == 1: # no alternate form
                column_name = column['name']
                column_elements = self.get_elements_for_column_name(column, column_name, 0)
                cur_column_data['name'] = column_name
                cur_column_data['elements'] = column_elements
                cur_column_data['index'] = column_idx
                res_column_data.append(cur_column_data)
            else:
                for i in range(len(column['forms'])):
                    column_name = column['name'] + "@" + column['forms'][i]['name']
                    column_elements = self.get_elements_for_column_name(column, column['name'], i)
                    cur_column_data['name'] = column_name
                    cur_column_data['elements'] = column_elements
                    cur_column_data['index'] = column_idx
                    res_column_data.append(cur_column_data)
            column_idx = column_idx + 1

        return res_column_data


    def parse_metrics(self, json):
        idx = 0
        metric_elements = {}
        for column in json['definition']['grid']['columns']:
            if column['name'] == "Metrics":
                metric_elements = {
                    'name': column['name'],
                    'elements': [element['name'] for element in column['elements']],
                    'index': idx
                }
                break
            idx = idx + 1

        return metric_elements
        

    def parse(self, json):

        if self._row_elements is None or self._column_elements is None:
            # gets the row's elements including the row's different formValues
            self._row_elements = self.parse_rows(json)
            self._column_elements = self.parse_columns(json)
            self._metric_elements = self.parse_metrics(json)
            self._column_elements.append(self._metric_elements)

            row_index_map = json['data']['headers']['rows']
            column_index_map = json['data']['headers']['columns']

            header_data_rows = self.map_index(self._row_elements, row_index_map) 
            header_data_columns = self.map_index(self._column_elements, (np.array(column_index_map).T).tolist())
            self._mi_rows = [np.array(header_data_rows).T.tolist()] # Transposed to match pandas MultiIndex dimmension
            self._mi_cols = [np.array(header_data_columns).T.tolist()] # Transposed to match pandas MultiIndex dimmension

            self._row_names = list(map(lambda x: x['name'], self._row_elements))
            self._col_names = list(map(lambda x: x['name'], self._column_elements))

            self._raw_data = [(json['data']['metricValues']['raw'])]
            self._formatted_data = [(json['data']['metricValues']['formatted'])]
        else:        
            self._row_elements = self.parse_rows(json)

            row_index_map = json['data']['headers']['rows']
            header_data_rows = self.map_index(self._row_elements, row_index_map)
            
            mi_row = np.array(header_data_rows).T.tolist()

            # append new row and metric data
            self._mi_rows.append(mi_row)
            self._raw_data += [(json['data']['metricValues']['raw'])]
            self._formatted_data += [(json['data']['metricValues']['formatted'])]



    def _to_dataframe(self, raw=False):
        # combine all data batches into one dataframe 

        res = [] # list of dataframes
        mi_cols = None # column index 

        if len(self._mi_cols[0]) > 1:
            # use pandas MultiIndex
            mi_cols = pd.MultiIndex.from_arrays(self._mi_cols[0], names=self._col_names) 
        else:
            # normal column index
            mi_cols = self._mi_cols[0][0] 

        if raw:
            # return raw data
            for raw, row in zip(self._raw_data, self._mi_rows):
                mi_rows = pd.MultiIndex.from_arrays(row, names=self._row_names)
                df = pd.DataFrame(index=mi_rows, columns=mi_cols, data=raw)
                res.append(df)     
        else:
            # return formatted data
            for formatted, row in zip(self._formatted_data, self._mi_rows):
                mi_rows = pd.MultiIndex.from_arrays(row, names=self._row_names)
                df = pd.DataFrame(index=mi_rows, columns=mi_cols, data=formatted)
                res.append(df)     

        return pd.concat(res)

